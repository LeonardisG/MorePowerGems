package master.listeners;

import dev.iseal.powergems.PowerGems;
import master.gems.AffluenceGem;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * TradeListener handles villager trade discounts for players who have activated the Affluence Gem.
 * This listener applies a 50% discount on all items traded with villagers by halving both the
 * price multiplier and ingredient amounts.
 */
public class TradeListener implements Listener {   
    private final Logger logger = PowerGems.getPlugin().getLogger();
    public Map<Player, List<MerchantRecipe>> originalRecipes = new HashMap<>();
    /**
     * Handles inventory open events to detect when a player opens a villager trading menu.
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onTradeEvent(InventoryOpenEvent e) {
        try {
            // Check if this is a merchant inventory, the entity is a player, and the player has discount metadata
            if (e.getInventory() instanceof MerchantInventory merchantInventory &&
                    e.getPlayer() instanceof Player player &&
                    player.hasMetadata(AffluenceGem.DISCOUNT_METADATA_KEY)) {
                        

                originalRecipes.put(player, new ArrayList<>(merchantInventory.getMerchant().getRecipes()));
                // Get original recipes and create a container for discounted ones
                List<MerchantRecipe> recipes = merchantInventory.getMerchant().getRecipes();
                List<MerchantRecipe> discountedRecipes = new ArrayList<>();
                // Process each recipe to create a discounted version
                for (MerchantRecipe recipe : recipes) {
                    MerchantRecipe discountedRecipe = createDiscountedRecipe(recipe);
                    discountedRecipes.add(discountedRecipe);
                }
                merchantInventory.getMerchant().setRecipes(discountedRecipes);
            }
            } catch (Exception ex) {
            logger.warning("Error applying trade discounts: " + ex.getMessage());
        }
    }


            @EventHandler
            public void onInventoryClose(InventoryCloseEvent event) {
              Player player = (Player) event.getPlayer();
            if (event.getInventory() instanceof MerchantInventory &&
         originalRecipes.containsKey(player)) {
             MerchantInventory merchantInventory = (MerchantInventory) event.getInventory();
             {
                 
            // Reset merchant recipes to original
            merchantInventory.getMerchant().setRecipes(originalRecipes.get(player));
            originalRecipes.remove(player);
            
            }
        }    
    }
    /**
     * Creates a discounted version of a merchant recipe by:
     * 1. Halving the price multiplier
     * 2. Halving the amount of each ingredient (minimum 1)
     *
     * @param originalRecipe The original merchant recipe
     * @return A new MerchantRecipe with discounted prices
     */
    private MerchantRecipe createDiscountedRecipe(MerchantRecipe originalRecipe) {
        // Create a new recipe with halved price multiplier
        MerchantRecipe discountedRecipe = new MerchantRecipe(
                originalRecipe.getResult(),
                originalRecipe.getUses(),
                originalRecipe.getMaxUses(),
                originalRecipe.hasExperienceReward(),
                originalRecipe.getVillagerExperience(),
                originalRecipe.getPriceMultiplier() / 2.0f
        );

        // Copy the ingredients but halve the amounts
        originalRecipe.getIngredients().forEach(item -> {
            if (item != null) {
                // Create a copy of the item with half the amount (minimum 1)
                item = item.clone();
                item.setAmount(Math.max(1, item.getAmount() / 2));
                discountedRecipe.addIngredient(item);
            }
        });

        return discountedRecipe;
    }
    public void cleanup() {
for(Map.Entry<Player, List<MerchantRecipe>> entry : originalRecipes.entrySet()) {
    Player player = entry.getKey();
    List <MerchantRecipe> recipes = entry.getValue();

    if(player.getOpenInventory().getTopInventory() instanceof MerchantInventory merchantInventory) {
        merchantInventory.getMerchant().setRecipes(recipes);
        player.closeInventory();
    }

}
        originalRecipes.clear();
    }
}