package master.gems;

import dev.iseal.powergems.misc.AbstractClasses.Gem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

import static dev.iseal.sealLib.SealLib.getPlugin;

public class MechGem extends Gem {
    public MechGem() {
        super("Mech");
    }

    // Creates explosion at player's eye location
    @Override
    protected void rightClick(Player player, int level) {
      Location location = player.getEyeLocation();
        Block block = location.getBlock();
        block.getWorld().createExplosion(block.getLocation(), 2.0F + level);
    }

    // Places lava in a radius around the player
    @Override
    protected void leftClick(Player player, int level) {
        int radius = 2;
        Location castLocation = player.getLocation().clone();

        for(int x = -radius; x <= radius; x++) {
            for(int y = -radius; y <= radius; y++) {
                for(int z = -radius; z <= radius; z++) {
                    Block block = castLocation.clone().add(x, y, z).getBlock();

                    if (block.getType() == Material.BEDROCK ||
                            block.getType() == Material.BARRIER ||
                            block.getType().name().contains("SHULKER_BOX") ||
                            block.getType().name().contains("CHEST")) {
                        continue;
                    }
                    if (block.getWorld().getSpawnLocation().distance(block.getLocation()) < 50) {
                        continue;
                    }
                    if (block.isLiquid()) continue;

                    block.setType(Material.LAVA);
                }
            }
        }

        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            for(int x = -radius; x <= radius; x++) {
                for(int y = -radius; y <= radius; y++) {
                    for(int z = -radius; z <= radius; z++) {
                        Block block = castLocation.clone().add(x, y, z).getBlock();
                        if (block.getType() == Material.LAVA) {
                            block.setType(Material.AIR);
                        }
                    }
                }
            }
        }, 200L + (20L * level));
    }

    // Creates a temporary magma block box around the player
    @Override
    protected void shiftClick(Player player, int level) {
        Location playerLoc = player.getLocation();
        ArrayList<FallingBlock> blocks = new ArrayList<>();

        // Create walls (3 blocks tall, hollow inside)
        for (int y = 0; y < 3; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    // Skip center and corners
                    if ((x == 0 && z == 0) || (Math.abs(x) == 1 && Math.abs(z) == 1)) {
                        continue;
                    }

                    Location blockLoc = playerLoc.clone().add(x, y, z);

                    if (blockLoc.getBlock().getType() == Material.AIR) {
                        FallingBlock fallingBlock = player.getWorld().spawnFallingBlock(
                                blockLoc,
                                Material.MAGMA_BLOCK.createBlockData()
                        );

                        fallingBlock.setDropItem(false);
                        fallingBlock.setHurtEntities(false);
                        fallingBlock.setGravity(false);
                        fallingBlock.setInvulnerable(true);
                        blocks.add(fallingBlock);
                    }
                }
            }
        }

        // Create floor (3x3 platform below player)
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location blockLoc = playerLoc.clone().add(x, -1, z);

                if (blockLoc.getBlock().getType() == Material.AIR) {
                    FallingBlock fallingBlock = player.getWorld().spawnFallingBlock(
                            blockLoc,
                            Material.MAGMA_BLOCK.createBlockData()
                    );

                    fallingBlock.setDropItem(false);
                    fallingBlock.setHurtEntities(false);
                    fallingBlock.setGravity(false);
                    fallingBlock.setInvulnerable(true);
                    blocks.add(fallingBlock);
                }
            }
        }

        // Create roof (3x3 ceiling at y=3)
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location blockLoc = playerLoc.clone().add(x, 3, z);

                if (blockLoc.getBlock().getType() == Material.AIR) {
                    FallingBlock fallingBlock = player.getWorld().spawnFallingBlock(
                            blockLoc,
                            Material.MAGMA_BLOCK.createBlockData()
                    );

                    fallingBlock.setDropItem(false);
                    fallingBlock.setHurtEntities(false);
                    fallingBlock.setGravity(false);
                    fallingBlock.setInvulnerable(true);
                    blocks.add(fallingBlock);
                }
            }
        }

        Bukkit.getScheduler().runTaskLater(
                getPlugin(),
                () -> blocks.forEach(FallingBlock::remove),
                200 + (20L * level)
        );
    }



    @Override
    public ArrayList<String> getDefaultLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_RED + "Level %level%");
        lore.add(ChatColor.DARK_RED + "Abilities");
        lore.add(ChatColor.WHITE + "Right Click: Create an explosion");
        lore.add(ChatColor.WHITE + "Left Click: Place temporary lava around you");
        lore.add(ChatColor.WHITE + "Shift Click: Create a protective magma box");
        return lore;
    }

    @Override
    public PotionEffectType getDefaultEffectType() {
        return PotionEffectType.FIRE_RESISTANCE;
    }

    @Override
    public int getDefaultEffectLevel() {
        return 0;
    }

    @Override
    public Particle getDefaultParticle() {
        return Particle.LAVA;
    }

    @Override
    public BlockData getParticleBlockData() {
        return null;
    }
}
