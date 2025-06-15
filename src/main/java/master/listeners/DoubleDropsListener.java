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

/**
 * Listener that handles doubling drops from blocks and entities.
 * Triggers when players with the Affluence Gem ability break blocks or kill entities.
 */
public class DoubleDropsListener implements Listener {

    /**
     * Handles block breaking events for players with double drops ability.
     * Replaces normal block drops with double the amount.
     *
     * @param e The BlockBreakEvent containing information about the broken block
     */
    @EventHandler
    public void dropEvent(BlockBreakEvent e) {
        if (e.getPlayer().hasMetadata(AffluenceGem.DOUBLE_DROPS_METADATA_KEY)) {
            Block block = e.getBlock(); //The original block
            Material blockType = block.getType(); //Its material type
            Location blockLocation = block.getLocation(); //Its location

            e.setDropItems(false); // Prevent default drops
            ItemStack drop = new ItemStack(blockType, 2); // The original block but doubled
            block.getWorld().dropItemNaturally(blockLocation, drop);
        }
    }

    /**
     * Handles entity death events for players with double drops ability.
     * Replaces normal entity drops with double the amount.
     *
     * @param e The EntityDeathEvent containing information about the killed entity
     */
    @EventHandler
    public void killEvent(EntityDeathEvent e) {
        // Safely check if the entity was killed by a player
        if (e.getEntity().getKiller() != null && e.getEntity().getKiller().hasMetadata(AffluenceGem.DOUBLE_DROPS_METADATA_KEY)) {
            ItemStack[] drops = e.getDrops().toArray(new ItemStack[0]); // Get the original drops
            e.getDrops().clear(); // Clear the original drops

            for (ItemStack drop : drops) {
                if (drop != null && drop.getType() != Material.AIR) {
                    ItemStack doubledDrop = drop.clone(); // Clone to preserve all item properties
                    doubledDrop.setAmount(drop.getAmount() * 2); // Double the amount
                    e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), doubledDrop); // Drop the doubled item
                }
            }
        }
    }
}