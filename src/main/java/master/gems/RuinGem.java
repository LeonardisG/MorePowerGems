package master.gems;

import dev.iseal.powergems.misc.AbstractClasses.Gem;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class RuinGem extends Gem {

    public RuinGem() { super("Ruin"); }

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    /** Grapples to blocks in line of sight. */
    @Override
    protected void leftClick(Player player, int level) { grapple(level, player); }

    /** Transforms surrounding blocks into moss. */
    @Override
    protected void rightClick(Player player, int level) {
        int radius = 5 + (level * 2);
        List<Location> blocksAround = getBlocksAroundPlayer(player, radius);
        replaceBlocks(player, blocksAround);
    }

    /** Creates an infestation of silverfish that spread and infest blocks. */
    @Override
    protected void shiftClick(Player player, int level) {
        int[] totalSpawned = {0};
        int maxTotal = Math.min(10 + (level * 10), 75);
        int depth = 25;
        spawnSpecialSilverFish(player, player.getLocation(), level, depth, totalSpawned, maxTotal);
    }

    @Override
    public ArrayList<String> getDefaultLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Level %level%");
        lore.add(ChatColor.GREEN + "Abilities");
        lore.add(ChatColor.WHITE + "Right click: Transform surrounding blocks into moss.");
        lore.add(ChatColor.WHITE + "Shift click: Makes an infestation of silverfish that spread and infest blocks.");
        lore.add(ChatColor.WHITE + "Left click: Grapple to blocks in your line of sight.");
        return lore;
    }

    @Override
    public int getDefaultEffectLevel() { return 1; }

    @Override
    public Particle getDefaultParticle() {
        return null;
    }

    @Override
    public PotionEffectType getDefaultEffectType() { return PotionEffectType.INVISIBILITY; }

    @Override
    public BlockData getParticleBlockData() {
        return null;
    }

    private void grapple(int level, Player player) {
        if (player == null) return;
        Vector direction = player.getLocation().getDirection();
        double maxDistance = 10 * level;
        Location startLocation = player.getEyeLocation();
        RayTraceResult result = player.getWorld().rayTraceBlocks(startLocation, direction, maxDistance);
        if (result != null && result.getHitBlock() != null) {
            Location hitLoc = result.getHitPosition().toLocation(player.getWorld());
            drawLaser(startLocation, hitLoc);
            Vector pullVector = hitLoc.toVector().subtract(player.getLocation().toVector()).normalize().multiply(3);
            player.setVelocity(pullVector);
        }
    }

    private void drawLaser(Location start, Location end) {
        if (start == null || end == null || !Objects.equals(start.getWorld(), end.getWorld())) return;
        double distance = start.distance(end);
        Vector direction = end.toVector().subtract(start.toVector()).normalize();
        for (double i = 0; i < distance; i += 0.5) {
            Vector point = start.toVector().add(direction.clone().multiply(i));
            Location particleLoc = point.toLocation(Objects.requireNonNull(start.getWorld()));
            start.getWorld().spawnParticle(
                    Particle.ELECTRIC_SPARK,
                    particleLoc,
                    1, 0, 0, 0, 0,
                    new Particle.DustOptions(Color.RED, 1.0f)
            );
        }
    }

    private List<Location> getBlocksAroundPlayer(Player player, int radius) {
        List<Location> blocks = new ArrayList<>();
        Location playerLocation = player.getLocation();
        World world = player.getWorld();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                double distance = Math.sqrt(x * x + z * z);
                if (distance > radius) continue;
                int blockX = playerLocation.getBlockX() + x;
                int blockZ = playerLocation.getBlockZ() + z;
                int blockY = world.getHighestBlockYAt(blockX, blockZ);
                blocks.add(new Location(world, blockX, blockY, blockZ));
            }
        }
        return blocks;
    }

    private void replaceBlocks(Player player, List<Location> locations) {
        World world = player.getWorld();
        for (Location loc : locations) {
            Block block = world.getBlockAt(loc);
            Material type = block.getType();
            if (!type.isAir() && type != Material.INFESTED_STONE && type != Material.WATER && type != Material.END_PORTAL_FRAME &&
                type != Material.LAVA && type != Material.BEDROCK && type != Material.OBSIDIAN && type != Material.BARRIER &&
                type != Material.END_PORTAL && type != Material.ENDER_DRAGON_SPAWN_EGG) {
                block.setType(Material.MOSS_BLOCK);
            }
        }
    }

    private void spawnSpecialSilverFish(Player plr, Location loc, int level, int remainingDepth, int[] totalSpawned, int maxTotal) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("MorePowerGems");
        if (plugin == null) return;
        if (remainingDepth <= 0) return;
        if (totalSpawned[0] >= maxTotal) return;

        Silverfish silverfish = (Silverfish) plr.getWorld().spawnEntity(loc, EntityType.SILVERFISH);
        totalSpawned[0]++;
        silverfish.setCustomName("Ruin Silverfish");
        silverfish.setCustomNameVisible(false);

        // Auto remove after lifetime
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (silverfish.isValid()) silverfish.remove();
        }, 400);

        final Location[] lastLocation = {silverfish.getLocation().clone()};

        // Block infesting task
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!silverfish.isValid()) {
                    cancel();
                    return;
                }
                Location currentLocation = silverfish.getLocation();
                if (lastLocation[0].getBlockX() != currentLocation.getBlockX() ||
                    lastLocation[0].getBlockY() != currentLocation.getBlockY() ||
                    lastLocation[0].getBlockZ() != currentLocation.getBlockZ()) {
                    Block blockBelow = currentLocation.getBlock().getRelative(0, -1, 0);
                    Material blockType = blockBelow.getType();
                    if (!blockType.isAir() && blockType.isSolid() &&
                            blockType != Material.INFESTED_STONE && blockType != Material.WATER &&
                            blockType != Material.END_PORTAL_FRAME && blockType != Material.LAVA &&
                            blockType != Material.BEDROCK && blockType != Material.OBSIDIAN &&
                            blockType != Material.BARRIER && blockType != Material.END_PORTAL &&
                            blockType != Material.ENDER_DRAGON_SPAWN_EGG) {
                        blockBelow.setType(Material.INFESTED_STONE);
                    }
                    lastLocation[0] = currentLocation.clone();
                }
            }
        }.runTaskTimer(plugin, 0L, 5L);

        // Reproduction control per silverfish
        final int[] childrenSpawned = {0};
        int maxChildrenForThis = Math.min(level + 2, 8);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!silverfish.isValid()) {
                    cancel();
                    return;
                }
                if (childrenSpawned[0] >= maxChildrenForThis) {
                    cancel();
                    return;
                }
                if (totalSpawned[0] >= maxTotal) {
                    cancel();
                    return;
                }
                if (remainingDepth - 1 <= 0) {
                    cancel();
                    return;
                }

                Location spawnLoc = silverfish.getLocation().clone().add((Math.random() * 2) - 1, 0, (Math.random() * 2) - 1);
                spawnSpecialSilverFish(plr, spawnLoc, level, remainingDepth - 1, totalSpawned, maxTotal);
                childrenSpawned[0]++;
            }
        }.runTaskTimer(plugin, 40L, 40L);
    }
}

