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

                e.setDropItems(false); // Prevent default drops
                ItemStack drop = new ItemStack(blockType, 2); // The original block but doubled
                block.getWorld().dropItemNaturally(blockLocation, drop);

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