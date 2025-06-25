package master.gems;

import dev.iseal.powergems.misc.AbstractClasses.Gem;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * RuinGem has the following abilities:
 * <p>
 * Left-click: Grapples the player towards a target block in their line of sight.
 * <p>
 * Right-click: Transforms blocks around the player into moss blocks.
 * <p>
 * Shift-click: Spawns a special silverfish that infests blocks it walks over.
 */
public class RuinGem extends Gem {

    /**
     * Constructs the RuinGem.
     */
    public RuinGem() {
        super("Ruin");
    }

    /**
     * Processes the player's action
     *
     * @param act  the action performed
     * @param plr  the player using the gem
     * @param item the gem item
     */
    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    /**
     * Left-click: Grapples the player towards a target block in the direction they're looking.
     * Uses a ray cast to find a valid target block and pull the player towards it.
     *
     * @param player the player who left-clicked
     * @param level  the level of the gem
     */
    @Override
    protected void leftClick(Player player, int level) {
        Grapple(level, player); // Grapple the player towards the target block
    }

    /**
     * Right-click: Transforms blocks around the player into moss blocks.
     * The radius increases with gem level.
     *
     * @param player the player who rights-clicked
     * @param level  the level of the gem
     */
    @Override
    protected void rightClick(Player player, int level) {
        int radius = 5 + (level * 2); // Radius increases with gem level

        // Get blocks around player and replace them with moss
        List<Location> blocksAround = getBlocksAroundPlayer(player, radius);
        replaceBlocks(player, blocksAround);
    }

    /**
     * Shift-click: Spawns a special silverfish that infests blocks it walks over.
     *
     * @param player the player who shifts-clicked
     * @param level  the level of the gem
     */
    @Override
    protected void shiftClick(Player player, int level) {
        spawnSpecialSilverFish(player, player.getLocation(), level, 25);
    }

    /**
     * Returns the default lore for this gem
     *
     * @return ArrayList of lore strings
     */
    @Override
    public ArrayList<String> getDefaultLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Level %level%");
        lore.add(ChatColor.GREEN + "Abilities");
        lore.add(ChatColor.WHITE
                + "Right click: Transform surrounding blocks into moss.");
        lore.add(ChatColor.WHITE
                + "Shift click: Makes an infestation of silverfish that spread and infest blocks.");
        lore.add(ChatColor.WHITE
                + "Left click: Grapple to blocks in your line of sight.");
        return lore;
    }

    /**
     * Returns the default effect level for this gem
     *
     * @return the default effect level
     */
    @Override
    public int getDefaultEffectLevel() {
        return 1;
    }

    /**
     * Returns the default potion effect type for this gem
     *
     * @return the default potion effect type
     */
    @Override
    public PotionEffectType getDefaultEffectType() {
        return PotionEffectType.INVISIBILITY;
    }

    /**
     * Grapples the player towards the block they are looking at.
     *
     * @param level  The gem level, determines maximum grapple distance (10 blocks × level)
     * @param player The player to grapple
     * @throws IllegalStateException if player is null
     */
    private void Grapple(int level, Player player) {
        if (player == null) {
            throw new IllegalStateException("Player cannot be null");
        }

        Vector direction = player.getLocation().getDirection();
        double maxDistance = 10 * level; // The Maximum distance the grapple can reach

        Location startLocation = player.getEyeLocation(); // Use eye location for better aiming
        RayTraceResult result = player.getWorld().rayTraceBlocks(startLocation, direction, maxDistance);

        if (result != null && result.getHitBlock() != null) {
            Location hitLoc = result.getHitPosition().toLocation(player.getWorld());

            drawLaser(startLocation, hitLoc);

            // Calculate pull vector
            Vector pullVector = hitLoc.toVector().subtract(player.getLocation().toVector());
            pullVector.normalize().multiply(3); // 1.5 is the velocity multiplier

            // Apply velocity to player
            player.setVelocity(pullVector);
        }
    }

    /**
     * Creates a particle-based laser effect between two locations.
     * Draws red particles in a line connecting the start and end points.
     *
     * @param start The starting location of the laser
     * @param end   The ending location of the laser
     */
    private void drawLaser(Location start, Location end) {
        if (start == null || end == null || !Objects.equals(start.getWorld(), end.getWorld())) {
            return; // Prevent exceptions if locations are invalid
        }

        double distance = start.distance(end);
        Vector direction = end.toVector().subtract(start.toVector()).normalize();

        // Draw particles along the line with 0.5 block spacing
        for (double i = 0; i < distance; i += 0.5) {
            Vector point = start.toVector().add(direction.clone().multiply(i));
            Location particleLoc = point.toLocation(Objects.requireNonNull(start.getWorld()));

            // Red laser particles with size 1.0
            start.getWorld().spawnParticle(
                    Particle.REDSTONE,
                    particleLoc,
                    1, 0, 0, 0, 0,  // Count and spread parameters
                    new Particle.DustOptions(Color.RED, 1.0f)
            );
        }
    }

    /**
     * Gets all surface blocks around the player
     *
     * @param player The player to center the search around
     * @param radius The radius to search for blocks
     * @return A list of locations of surface blocks within the radius
     */
    private List<Location> getBlocksAroundPlayer(Player player, int radius) {
        List<Location> blocks = new ArrayList<>();
        Location playerLocation = player.getLocation();
        World world = player.getWorld();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                // Calculate XZ distance for a circular pattern
                double distance = Math.sqrt(x*x + z*z);
                if (distance > radius) continue;

                // Get the block coordinates
                int blockX = playerLocation.getBlockX() + x;
                int blockZ = playerLocation.getBlockZ() + z;

                // Find the surface
                int blockY = world.getHighestBlockYAt(blockX, blockZ);

                // Create location at the surface block
                Location loc = new Location(world, blockX, blockY, blockZ);
                blocks.add(loc);
            }
        }

        return blocks;
    }

    /**
     * Replaces blocks with moss blocks.
     *
     * @param player    The player who initiated the action
     * @param locations The list of locations to replace blocks at
     */
    private void replaceBlocks(Player player, List<Location> locations) {
        World world = player.getWorld();

        for (Location loc : locations) {
            Block block = world.getBlockAt(loc);
            if (!block.getType().isAir()
                    && block.getType() != Material.INFESTED_STONE
                    && block.getType() != Material.WATER
                    && block.getType() != Material.END_PORTAL_FRAME
                    && block.getType() != Material.LAVA
                    && block.getType() != Material.BEDROCK
                    && block.getType() != Material.OBSIDIAN
                    && block.getType() != Material.BARRIER
                    && block.getType() != Material.END_PORTAL
                    && block.getType() != Material.ENDER_DRAGON_SPAWN_EGG) {
                block.setType(Material.MOSS_BLOCK);
            }
        }
    }

    /**
     * Spawns a special silverfish that transforms blocks it walks over,
     * multiplies every 2 seconds, and dies after 20 seconds.
     *
     * @param plr The player who spawned the silverfish
     * @param loc The location to spawn the silverfish at
     * @param level The level of the gem affecting silverfish properties
     * @param limit The maximum number of silverfish that can be spawned in total
     */
    private void spawnSpecialSilverFish(Player plr, Location loc, int level, int limit) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("MorePowerGems");
        if (plugin == null) return;

        // Check if we've reached the spawn limit
        if (limit <= 0) return;

        // Spawn the silverfish
        Silverfish silverfish = (Silverfish) plr.getWorld().spawnEntity(loc, EntityType.SILVERFISH);
        silverfish.setCustomName("Ruin Silverfish");
        silverfish.setCustomNameVisible(true);

        // Set death timer (20 seconds)
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (silverfish.isValid()) {
                silverfish.remove();
            }
        }, 400); // 400 ticks = 20 seconds

        // Track previous location to detect movement
        final Location[] lastLocation = {silverfish.getLocation().clone()};

        // Movement tracking and block replacement
        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
            // Cancel task if silverfish is gone
            if (!silverfish.isValid()) {
                task.cancel();
                return;
            }

            Location currentLocation = silverfish.getLocation();

            // Check if moved to a new block
            if (lastLocation[0].getBlockX() != currentLocation.getBlockX() ||
                    lastLocation[0].getBlockY() != currentLocation.getBlockY() ||
                    lastLocation[0].getBlockZ() != currentLocation.getBlockZ()) {

                // Replace block beneath with infested stone
                Block blockBelow = currentLocation.getBlock().getRelative(0, -1, 0);
                Material blockType = blockBelow.getType();

                // List of protected block types that should never be replaced
                if (blockType.isAir() ||
                        blockType == Material.INFESTED_STONE ||
                        blockType == Material.WATER ||
                        blockType == Material.END_PORTAL_FRAME ||
                        blockType == Material.LAVA ||
                        blockType == Material.BEDROCK ||
                        blockType == Material.OBSIDIAN ||
                        blockType == Material.BARRIER ||
                        blockType == Material.END_PORTAL ||
                        blockType == Material.ENDER_DRAGON_SPAWN_EGG) {
                    return;
                }

                blockBelow.setType(Material.INFESTED_STONE);

                // Update last location
                lastLocation[0] = currentLocation.clone();
            }
        }, 0, 5); // Check every 5 ticks

        // Multiplication timer (every 2 seconds = 40 ticks)
        final int[] multiplications = {0};
        final int maxMultiplications = level + 2; // Scale with gem level

        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
            // Cancel task if silverfish is gone or reached max multiplications
            if (!silverfish.isValid() || multiplications[0] >= maxMultiplications) {
                task.cancel();
                return;
            }

            // Create a new silverfish
            if (limit > 1) {
                Location spawnLoc = silverfish.getLocation().clone().add(
                        (Math.random() * 2) - 1, 0, (Math.random() * 2) - 1);
                spawnSpecialSilverFish(plr, spawnLoc, level, limit - 1);
                multiplications[0]++;
            }
        }, 40, 40); // 40 ticks = 2 seconds
    }
}