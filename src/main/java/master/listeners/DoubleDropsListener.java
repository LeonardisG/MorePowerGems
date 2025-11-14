package master.listeners;

import master.gems.AffluenceGem;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.bukkit.Material;

public class DoubleDropsListener implements Listener {

    /**
     * Handles block breaking events for players with double drops ability.
     */
    @EventHandler
    public void dropEvent(BlockBreakEvent e) {
        if (e.getPlayer().hasMetadata(AffluenceGem.DOUBLE_DROPS_METADATA_KEY)) {
            Block block = e.getBlock(); //The original block
            Material blockType = block.getType(); //Its material type
            Location blockLocation = block.getLocation(); //Its location

            if (blockType.name().endsWith("_ORE") || //Check if the block is an ore
                    blockType == Material.ANCIENT_DEBRIS) {

                e.setDropItems(false);

                ItemStack actualDrop;
                if (blockType == Material.DIAMOND_ORE || blockType == Material.DEEPSLATE_DIAMOND_ORE) {
                    actualDrop = new ItemStack(Material.DIAMOND, 2);
                } else if (blockType == Material.COAL_ORE || blockType == Material.DEEPSLATE_COAL_ORE) {
                    actualDrop = new ItemStack(Material.COAL, 2);
                } else if (blockType == Material.IRON_ORE || blockType == Material.DEEPSLATE_IRON_ORE) {
                    actualDrop = new ItemStack(Material.RAW_IRON, 2);
                } else if (blockType == Material.GOLD_ORE || blockType == Material.DEEPSLATE_GOLD_ORE) {
                    actualDrop = new ItemStack(Material.RAW_GOLD, 2);
                } else if (blockType == Material.COPPER_ORE || blockType == Material.DEEPSLATE_COPPER_ORE) {
                    actualDrop = new ItemStack(Material.RAW_COPPER, 4);
                } else if (blockType == Material.EMERALD_ORE || blockType == Material.DEEPSLATE_EMERALD_ORE) {
                    actualDrop = new ItemStack(Material.EMERALD, 2);
                } else if (blockType == Material.LAPIS_ORE || blockType == Material.DEEPSLATE_LAPIS_ORE) {
                    actualDrop = new ItemStack(Material.LAPIS_LAZULI, 8);
                } else if (blockType == Material.REDSTONE_ORE || blockType == Material.DEEPSLATE_REDSTONE_ORE) {
                    actualDrop = new ItemStack(Material.REDSTONE, 8);
                } else if (blockType == Material.NETHER_GOLD_ORE) {
                    actualDrop = new ItemStack(Material.GOLD_NUGGET, 4);
                } else if (blockType == Material.NETHER_QUARTZ_ORE) {
                    actualDrop = new ItemStack(Material.QUARTZ, 2);
                } else if (blockType == Material.ANCIENT_DEBRIS) {
                    actualDrop = new ItemStack(Material.ANCIENT_DEBRIS, 2);
                } else {
                    actualDrop = new ItemStack(blockType, 2);
                }

                block.getWorld().dropItemNaturally(blockLocation, actualDrop);
            }
        }
    }

    /**
     * Handles entity death events for players with double drops ability.
     */
    @EventHandler
    public void killEvent(EntityDeathEvent e) {
        if (e.getEntity().getKiller() != null && e.getEntity().getKiller().hasMetadata(AffluenceGem.DOUBLE_DROPS_METADATA_KEY)) {
            ItemStack[] drops = e.getDrops().toArray(new ItemStack[0]); // Get the original drops
            e.getDrops().clear(); // Clear the original drops

            for (ItemStack drop : drops) {
                if (drop != null && drop.getType() != Material.AIR) {
                    ItemStack doubledDrop = drop.clone();
                    doubledDrop.setAmount(drop.getAmount() * 2);
                    e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), doubledDrop);
                }
            }
        }
    }
}