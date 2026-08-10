package master.gems;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import dev.iseal.powergems.misc.AbstractClasses.Gem;
import static dev.iseal.sealLib.SealLib.getPlugin;
import master.MPG;

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
        HashMap<Location, Material> originalBlocks = new HashMap<>();

        Levelled lavaData = (Levelled) Material.LAVA.createBlockData();
        lavaData.setLevel(0);

        try {
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

                        originalBlocks.put(block.getLocation(), block.getType());
                        // No physics: adjacent water can't convert the lava to stone, and it
                        // can't spread onto blocks the restore map never recorded.
                        block.setBlockData(lavaData, false);
                    }
                }
            }
        } finally {
            // In a finally block so a throw mid-loop still restores whatever was already placed.
            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> originalBlocks.forEach((location, material) -> {
                Block block = location.getBlock();
                if (block.getType() == Material.LAVA) {
                    block.setType(material);
                }
            }), 200L + (20L * level));
        }
    }

    // Creates a temporary magma block box around the player
    @Override
    protected void shiftClick(Player player, int level) {
        Location playerLoc = player.getLocation().getBlock().getLocation();
        ArrayList<FallingBlock> blocks = new ArrayList<>();

        // Create walls (3 blocks tall, hollow inside)
        for (int y = 0; y < 3; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    // Skip center and corners
                    if ((x == 0 && z == 0)) {
                        continue;
                    }

                    Location blockLoc = playerLoc.clone().add(x, y, z);


                    if (blockLoc.getBlock().getType() == Material.AIR) {
                        FallingBlock fallingBlock = player.getWorld().spawn(
                                blockLoc,
                                FallingBlock.class,
                                fb -> fb.setBlockData(Material.MAGMA_BLOCK.createBlockData())
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
                    FallingBlock fallingBlock = player.getWorld().spawn(
                            blockLoc,
                            FallingBlock.class,
                            fb -> fb.setBlockData(Material.MAGMA_BLOCK.createBlockData())
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
                    FallingBlock fallingBlock = player.getWorld().spawn(
                            blockLoc,
                            FallingBlock.class,
                            fb -> fb.setBlockData(Material.MAGMA_BLOCK.createBlockData())
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
        lore.add("<gradient:#FF6644:#881100>Level <level></gradient>");
        lore.add("<gradient:#FF6644:#881100>Abilities</gradient>");
        lore.add("<white>Right Click: Create an explosion</white>");
        lore.add("<white>Left Click: Place temporary lava around you</white>");
        lore.add("<white>Shift Click: Create a protective magma box</white>");
        if(MPG.PassiveLoreEnabled) {lore.add("<aqua>Passive: Fire Resistance</aqua>");}
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
