package master.gems;

import dev.iseal.powergems.misc.AbstractClasses.Gem;
import master.Keys;
import master.MPG;
import org.bukkit.*;
import org.bukkit.Registry;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static dev.iseal.sealLib.SealLib.getPlugin;

public class AmethystGem extends Gem {
    public static final String TRAP_METADATA = "AMETHYST_TRAPPED";
    private static final int[][] CAGE_REL = buildCage();

    // Constructor
    public AmethystGem() {
        super("Amethyst");
    }

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    /** Throws an amethyst shard projectile that deals damage on hit. */
    @Override
    protected void rightClick(Player player, int level) {
        Snowball projectile = player.launchProjectile(Snowball.class);
        projectile.setItem(new ItemStack(Material.AMETHYST_SHARD));

        var pdc = projectile.getPersistentDataContainer();
        pdc.set(Keys.AMETHYST_PROJECTILE, PersistentDataType.BYTE, (byte) 1);
        pdc.set(Keys.AMETHYST_LEVEL, PersistentDataType.INTEGER, level);

        var dir = player.getEyeLocation().getDirection().normalize().multiply(1.3);
        projectile.setVelocity(dir);
        projectile.setGravity(false);
    }

    /** Locks a target player in a purpur cage, trapping and damaging them. */
    @Override
    protected void leftClick(Player caster, int level) {
        Player target = findTarget(caster, 12 + level * 2);

        if (target == null || target == caster) {
            caster.sendMessage(ChatColor.DARK_PURPLE + "No target player in sight.");
            return;
        }

        if (target.hasMetadata(TRAP_METADATA)) {
            caster.sendMessage(ChatColor.GRAY + "That player is already trapped.");
            return;
        }

        trapPlayer(caster, target, level);
    }

    /** Reveals nearby players by applying glowing effect. */
    @Override
    protected void shiftClick(Player player, int level) {
        World world = player.getWorld();
        Location loc = player.getLocation().add(0, 1, 0);

        // Spawn particles
        world.spawnParticle(Particle.END_ROD, loc, 40, 0.4, 0.6, 0.4, 0.01);

        // Apply glowing to nearby players
        int radius = 25;
        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof Player target && entity != player) {
                target.addPotionEffect(new PotionEffect(
                        PotionEffectType.GLOWING,
                        100 * level,
                        1,
                        true,
                        false
                ));
            }
        }

        // Ring visual effect
        new BukkitRunnable() {
            double angle = 0;
            int ticks = 0;

            @Override
            public void run() {
                if (!player.isOnline() || ticks++ >= 20) {
                    cancel();
                    return;
                }

                Location base = player.getLocation().add(0, 0.9, 0);
                for (int k = 0; k < 8; k++) {
                    double a = angle + Math.PI * 2 / 8 * k;
                    double x = Math.cos(a) * 0.6;
                    double y = Math.sin(angle * 2) * 0.12;
                    double z = Math.sin(a) * 0.6;

                    world.spawnParticle(Particle.CRIT, base.clone().add(x, y, z), 1, 0, 0, 0, 0);
                }
                angle += Math.PI / 16;
            }
        }.runTaskTimer(getPlugin(), 0L, 1L);
    }

    /** Finds the player the caster is looking at within range. */
    private Player findTarget(Player caster, int maxDist) {
        Location eye = caster.getEyeLocation();
        var dir = eye.getDirection().normalize();

        Player best = null;
        double bestDot = 0.87; // ~30 degree cone
        double maxSq = maxDist * (double) maxDist;

        for (Entity e : caster.getNearbyEntities(maxDist, maxDist, maxDist)) {
            if (!(e instanceof Player target) || target == caster) continue;

            double distSq = target.getLocation().distanceSquared(caster.getLocation());
            if (distSq > maxSq) continue;

            var toTarget = target.getEyeLocation().toVector().subtract(eye.toVector()).normalize();
            double dot = dir.dot(toTarget);

            if (dot > bestDot) {
                bestDot = dot;
                best = target;
            }
        }

        return best;
    }

    /** Traps a player in a purpur cage dealing non-lethal damage over time. */
    private void trapPlayer(Player caster, Player target, int level) {
        // Calculate duration: 10s base +2s per level
        int durationSec = 10 + Math.max(0, level - 1) * 2;
        int durationTicks = durationSec * 20;

        // Calculate damage: scales with level but non-lethal
        int hearts = switch (Math.min(level, 10)) {
            case 1 -> 3;
            case 2 -> 4;
            case 3 -> 5;
            case 4 -> 6;
            case 5 -> 7;
            default -> 6;
        };
        double damagePerSecond = hearts * 2.0 / durationSec;

        // Build the cage
        List<BlockState> replaced = buildCageStructure(target);
        if (replaced.isEmpty()) return;

        // Visual and audio feedback
        World world = target.getWorld();
        world.spawnParticle(Particle.WITCH, target.getLocation().add(0, 1, 0), 25, 0.5, 0.7, 0.5, 0.05);
        world.playSound(target.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1f, 1.1f);

        target.setMetadata(TRAP_METADATA, new FixedMetadataValue(getPlugin(), true));
        caster.sendMessage(ChatColor.LIGHT_PURPLE + "Trapped " + target.getName() + " for " + durationSec + "s");
        target.sendMessage(ChatColor.DARK_PURPLE + "You are trapped in amethyst! " + ChatColor.GRAY + "(Cannot kill you)");

        // Start damage task
        startDamageTask(target, durationSec, damagePerSecond, replaced);

        // Failsafe cleanup
        new BukkitRunnable() {
            @Override
            public void run() {
                restoreCage(replaced);
                target.removeMetadata(TRAP_METADATA, getPlugin());
            }
        }.runTaskLater(getPlugin(), durationTicks + 40L);
    }

    /** Builds the cage structure and returns replaced blocks. */
    private List<BlockState> buildCageStructure(Player target) {
        Location base = target.getLocation().getBlock().getLocation();
        World world = base.getWorld();
        if (world == null) return new ArrayList<>();

        List<BlockState> replaced = new ArrayList<>();

        for (int[] r : CAGE_REL) {
            Block block = world.getBlockAt(
                base.getBlockX() + r[0],
                base.getBlockY() + r[1],
                base.getBlockZ() + r[2]
            );

            Material type = block.getType();
            if (!(type.isAir() || type == Material.WATER || type == Material.CAVE_AIR)) {
                continue; // Only fill empty spaces
            }

            BlockState prev = block.getState();
            block.setType(getCageMaterial(r[0], r[1], r[2]), false);
            replaced.add(prev);
        }

        return replaced;
    }

    /** Determines the material for a cage block based on position. */
    private Material getCageMaterial(int x, int y, int z) {
        // Floor (Y=0) and roof (Y=4): purpur blocks
        if (y == 0 || y == 4) {
            return Material.PURPUR_BLOCK;
        }
        // Corner pillars (Y=1-3): purpur pillar
        else if (Math.abs(x) == 1 && Math.abs(z) == 1 && y >= 1 && y <= 3) {
            return Material.PURPUR_PILLAR;
        }
        // Walls (Y=1-2): iron bars
        else {
            return Material.IRON_BARS;
        }
    }

    /** Starts the damage-over-time task for trapped player. */
    private void startDamageTask(Player target, int durationSec, double damagePerSecond, List<BlockState> replaced) {
        new BukkitRunnable() {
            int elapsed = 0;

            @Override
            public void run() {
                if (!target.isOnline() || target.isDead() || elapsed >= durationSec) {
                    finish();
                    return;
                }

                // Apply damage (non-lethal)
                double newHealth = Math.max(1.0, target.getHealth() - damagePerSecond);
                if (newHealth < target.getHealth()) {
                    target.setHealth(newHealth);
                }

                // Particle effect
                target.getWorld().spawnParticle(
                    Particle.END_ROD,
                    target.getLocation().add(0, 1, 0),
                    6, 0.3, 0.4, 0.3, 0.01
                );

                elapsed++;
            }

            void finish() {
                restoreCage(replaced);
                target.removeMetadata(TRAP_METADATA, getPlugin());
                cancel();
            }
        }.runTaskTimer(getPlugin(), 20L, 20L);
    }

    /** Restores blocks to their original state after trap expires. */
    private void restoreCage(List<BlockState> replaced) {
        for (BlockState state : replaced) {
            Block block = state.getBlock();
            Material currentType = block.getType();

            // Only restore if still part of cage
            if (currentType == Material.PURPUR_BLOCK ||
                currentType == Material.PURPUR_PILLAR ||
                currentType == Material.IRON_BARS) {
                state.update(true, false);
            }
        }
    }
    /** Builds the cage coordinate array: 1x1 prison cell with purpur pillars and iron bar windows. */
    private static int[][] buildCage() {
        List<int[]> list = new ArrayList<>();

        // Floor: 3x3 purpur blocks at Y=0
        addFloorAndRoof(list, 0);

        // Corner pillars: 4 purpur pillars at Y=1,2,3
        addCornerPillars(list);

        // Walls: Iron bars at Y=1,2 on all 4 sides
        addIronBarWalls(list);

        // Roof: 3x3 purpur blocks at Y=4
        addFloorAndRoof(list, 4);

        return list.toArray(new int[0][]);
    }

    private static void addFloorAndRoof(List<int[]> list, int y) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                list.add(new int[]{x, y, z});
            }
        }
    }

    private static void addCornerPillars(List<int[]> list) {
        int[] corners = {-1, 1};
        for (int cx : corners) {
            for (int cz : corners) {
                for (int y = 1; y <= 3; y++) {
                    list.add(new int[]{cx, y, cz});
                }
            }
        }
    }

    private static void addIronBarWalls(List<int[]> list) {
        for (int y = 1; y <= 2; y++) {
            list.add(new int[]{0, y, -1});  // North wall
            list.add(new int[]{0, y, 1});   // South wall
            list.add(new int[]{1, y, 0});   // East wall
            list.add(new int[]{-1, y, 0});  // West wall
        }
    }


    @Override
    public ArrayList<String> getDefaultLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "Level %level%");
        lore.add(ChatColor.LIGHT_PURPLE + "Abilities");
        lore.add(ChatColor.WHITE + "Right Click: Throw an amethyst shard");
        lore.add(ChatColor.WHITE + "Shift Click: Shines light on nearby players");
        lore.add(ChatColor.WHITE + "Left Click: Traps a player in a purpur cage, dealing non-lethal damage");
        if(MPG.PassiveLoreEnabled) {lore.add(ChatColor.AQUA + "Passive: Absorption");}
        return lore;
    }

    @Override
    public PotionEffectType getDefaultEffectType() {
        return PotionEffectType.ABSORPTION;
    }

    @Override
    public int getDefaultEffectLevel() {
        return 1;
    }

    @Override
    public Particle getDefaultParticle() {
        return Particle.WITCH;
    }

    @Override
    public BlockData getParticleBlockData() {
        return null;
    }
}
