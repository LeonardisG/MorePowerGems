package master.gems;

import dev.iseal.powergems.misc.AbstractClasses.Gem;
import master.Keys;
import org.bukkit.*;
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

    // Relative boundary blocks of a hollow 3x3x3 (y 0..2) cage around center
    private static final int[][] CAGE_REL = buildCage();
    private static int[][] buildCage() {
        List<int[]> list = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 2; y++) {
                for (int z = -1; z <= 1; z++) {
                    boolean boundary = Math.abs(x) == 1 || y == 0 || y == 2 || Math.abs(z) == 1;
                    if (boundary) list.add(new int[]{x,y,z});
                }
            }
        }
        return list.toArray(new int[0][]);
    }

    public AmethystGem() { super("Amethyst"); }

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
        projectile.setVelocity(player.getLocation().getDirection().multiply(1.3));
        var dir = player.getEyeLocation().getDirection();
        dir.normalize().multiply(1.3);
        dir.setY(0.5);
        projectile.setVelocity(dir);
        projectile.setGravity(false);
    }

    /** Locks a target player in an amethyst cage, trapping and damaging them. */
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
        player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(0, 1, 0), 40, 0.4, 0.6, 0.4, 0.01);
        int radius = 25;
        List<Entity> nearbyEntities = player.getNearbyEntities(radius, radius, radius);

        for (Entity entity : nearbyEntities) {
            if (entity instanceof Player && entity != player) {
                ((Player) entity).addPotionEffect(new PotionEffect(
                        PotionEffectType.GLOWING,
                        100 * level,
                        1,
                        true,
                        false
                ));
            }
        }
        new BukkitRunnable() { // simple ring visual 1s
            double angle = 0; int ticks = 0;
            @Override public void run() {
                if (!player.isOnline() || ticks++ >= 20) { cancel(); return; }
                var base = player.getLocation().add(0, 0.9, 0);
                for (int k = 0; k < 8; k++) {
                    double a = angle + Math.PI * 2 / 8 * k;
                    player.getWorld().spawnParticle(Particle.CRIT,
                            base.clone().add(Math.cos(a)*0.6, Math.sin(angle*2)*0.12, Math.sin(a)*0.6),
                            1,0,0,0,0);
                }
                angle += Math.PI / 16;
            }
        }.runTaskTimer(getPlugin(), 0L, 1L);
    }

    /** Finds the player the caster is looking at within range. */
    private Player findTarget(Player caster, int maxDist) {
        var eye = caster.getEyeLocation();
        var dir = eye.getDirection().normalize();
        Player best = null; double bestDot = 0.87; double maxSq = maxDist * (double)maxDist;
        for (Entity e : caster.getNearbyEntities(maxDist, maxDist, maxDist)) {
            if (e instanceof Player p && p != caster) {
                double distSq = p.getLocation().distanceSquared(caster.getLocation());
                if (distSq > maxSq) continue;
                var to = p.getEyeLocation().toVector().subtract(eye.toVector()).normalize();
                double dot = dir.dot(to);
                if (dot > bestDot) { bestDot = dot; best = p; }
            }
        }
        return best;
    }

    /** Traps a player in an amethyst cage dealing damage over time. */
    private void trapPlayer(Player caster, Player target, int level) {
        // Duration scaling: 10s base +2s per extra level
        int durationSec = 10 + Math.max(0, level - 1) * 2;
        int durationTicks = durationSec * 20;

        // Total hearts of damage spread per second (non-lethal)
        int hearts;
        switch (Math.min(level, 10)) {
            case 1 -> hearts = 3; // 6 hp
            case 2 -> hearts = 4; // 8 hp
            case 3 -> hearts = 5; // 10 hp
            case 4 -> hearts = 6; // 12 hp
            default -> hearts = 6; // cap
        }
        double totalDamage = hearts * 2.0;
        double damagePerSecond = totalDamage / durationSec; // applied each second

        // Build cage; record replaced states
        Location base = target.getLocation().getBlock().getLocation();
        World world = base.getWorld(); if (world == null) return;
        List<BlockState> replaced = new ArrayList<>();
        for (int[] r : CAGE_REL) {
            Block b = world.getBlockAt(base.getBlockX()+r[0], base.getBlockY()+r[1], base.getBlockZ()+r[2]);
            Material t = b.getType();
            if (!(t.isAir() || t == Material.WATER || t == Material.CAVE_AIR)) continue; // only fill empties
            BlockState prev = b.getState();
            b.setType(Material.AMETHYST_BLOCK, false);
            replaced.add(prev);
        }

        world.spawnParticle(Particle.WITCH, target.getLocation().add(0,1,0), 25, 0.5,0.7,0.5,0.05);
        world.playSound(target.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1f, 1.1f);

        target.setMetadata(TRAP_METADATA, new FixedMetadataValue(getPlugin(), true));
        caster.sendMessage(ChatColor.LIGHT_PURPLE + "Trapped " + target.getName() + " for " + durationSec + "s");
        target.sendMessage(ChatColor.DARK_PURPLE + "You are trapped in amethyst!" + ChatColor.GRAY + " (Cannot kill you)");

        // Damage task (runs each second)
        new BukkitRunnable() {
            int elapsed = 0;
            @Override public void run() {
                if (!target.isOnline() || target.isDead()) { finish(); return; }
                if (elapsed >= durationSec) { finish(); return; }
                double newHealth = Math.max(1.0, target.getHealth() - damagePerSecond);
                if (newHealth < target.getHealth()) target.setHealth(newHealth);
                target.getWorld().spawnParticle(Particle.END_ROD, target.getLocation().add(0,1,0), 6, 0.3,0.4,0.3,0.01);
                elapsed++;
            }
            void finish() {
                restore(replaced);
                target.removeMetadata(TRAP_METADATA, getPlugin());
                cancel();
            }
        }.runTaskTimer(getPlugin(), 20L, 20L); // start after 1s for clear feedback

        // Failsafe cleanup
        new BukkitRunnable() {
            @Override public void run() { restore(replaced); target.removeMetadata(TRAP_METADATA, getPlugin()); }
        }.runTaskLater(getPlugin(), durationTicks + 40L); // small buffer
    }

    /** Restores blocks to their original state after trap expires. */
    private void restore(List<BlockState> replaced) {
        for (BlockState s : replaced) {
            Block b = s.getBlock();
            if (b.getType() == Material.AMETHYST_BLOCK) s.update(true, false);
        }
    }

    @Override public ArrayList<String> getDefaultLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "Level %level%");
        lore.add(ChatColor.LIGHT_PURPLE + "Abilities");
        lore.add(ChatColor.WHITE +  "Right Click: Throw an amethyst shard");
        lore.add(ChatColor.WHITE + "Shift Click: Shines light on nearby players");
        lore.add(ChatColor.WHITE + "Left Click: Traps a player in an amethyst cage, dealing non-lethal damage");
        return lore;
    }
    @Override public PotionEffectType getDefaultEffectType() { return PotionEffectType.RESISTANCE; }
    @Override public int getDefaultEffectLevel() { return 1; }
    @Override public Particle getDefaultParticle() { return Particle.WITCH; }
    @Override public BlockData getParticleBlockData() { return null; }
}
