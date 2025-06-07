package Master.listeners;

import Master.gems.AffluenceGem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static dev.iseal.sealLib.SealLib.getPlugin;

/**
 * TradeListener handles villager trade discounts for players who have activated the Affluence Gem.
 * This listener applies a 50% discount on all items traded with villagers by halving both the
 * price multiplier and ingredient amounts.
 */
public class TradeListener implements Listener {

    private final Logger logger;

    /**
     * Constructor that initializes the logger for better error tracking.
     */
    public TradeListener() {
        Plugin plugin = getPlugin();
        this.logger = plugin.getLogger();
    }

    /**
     * Handles inventory open events to detect when a player opens a villager trading menu.
     * When detected, it checks if the player has the Affluence Gem discount metadata and
     * applies a 50% discount to all trades.
     *
     * @param e The InventoryOpenEvent that triggered this handler
     */
    @EventHandler(priority = EventPriority.NORMAL)
    private void onTradeEvent(InventoryOpenEvent e) {
        try {
            // Check if this is a merchant inventory and if the entity is a player
            if (e.getInventory() instanceof MerchantInventory merchantInventory && e.getPlayer() instanceof Player player) {
                // Check if player has the discount metadata from the AffluenceGem
                if (player.hasMetadata(AffluenceGem.DISCOUNT_METADATA_KEY)) {
                    logger.fine("Applying trade discount for player: " + player.getName());

                    // Get original recipes and create a container for discounted ones
                    List<MerchantRecipe> recipes = merchantInventory.getMerchant().getRecipes();
                    List<MerchantRecipe> discountedRecipes = new ArrayList<>();

                    // Process each recipe to create a discounted version
                    for (MerchantRecipe recipe : recipes) {
                        MerchantRecipe discountedRecipe = createDiscountedRecipe(recipe);
                        discountedRecipes.add(discountedRecipe);
                    }

                    // Apply the discounted recipes to the merchant
                    merchantInventory.getMerchant().setRecipes(discountedRecipes);
                    logger.fine("Successfully applied discounts to " + discountedRecipes.size() + " trades");
                }
            }
        } catch (Exception ex) {
            // Log any errors that occur during discount application
            logger.warning("Error applying trade discounts: " + ex.getMessage());
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
}