package master.gems;

import master.utils.Utils;
import dev.iseal.powergems.misc.AbstractClasses.Gem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

import static dev.iseal.sealLib.SealLib.getPlugin;

/**
 * AffluenceGem has the following abilities:
 * <p>
 * Left-click:
 * Gives player double drops for 20-second times the level of the gem.
 * <p>
 * Right-click:
 * Gives player haste 2 for 30-second times the level of the gem.
 * <p>
 * Shift-click:
 * Halves all trade prices for 60 seconds times the level of the gem.
 */
public class AffluenceGem extends Gem {

    /** Multiplier for HASTE_DURATION_SECONDS. */
    private static final int RIGHT_CLICK_POTION_EFFECT_TIME = 20;

    /** Duration in ticks for double drops effect per level. */
    private static final long DOUBLE_DROPS_DURATION_TICKS = 400L;

    /** Duration in seconds for double drops effect. */
    private static final int DOUBLE_DROPS_DURATION_SECONDS = 20;

    /** Duration in seconds for haste. */
    private static final int HASTE_DURATION_SECONDS = 30;

    /** Duration in ticks for trade discount effect. */
    private static final long DISCOUNT_DURATION_TICKS = 1200L;

    /** Duration in seconds for trade discount effect. */
    private static final int DISCOUNT_DURATION_SECONDS = 60;

    /**
     * Metadata key for halving trade prices effect.
     * This key is used to check if
     * the player has the halved price effect active.
     */
    public static final String DISCOUNT_METADATA_KEY = "HALVE_PRICE";

    /**
     * Metadata key for double drops effect.
     * This key is used to check
     * if the player has the double drops effect active.
     */
    public static final String DOUBLE_DROPS_METADATA_KEY = "DOUBLE_DROPS";

    /**
     * Constructs the AffluenceGem.
     */
    public AffluenceGem() {
        super("Affluence");
    }

    /**
     * Processes the player's action.
     *
     * @param act  the action performed
     * @param plr  the player using the gem
     * @param item the gem item
     */
    @Override
    public void call(final Action act, final Player plr, final ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    /**
     * Left-click: Gives player double drops for 20-second times the level of
     * the gem.
     *
     * @param player the player who left-clicked
     * @param level  the level of the gem
     */
    @Override
    protected void leftClick(final Player player, final int level) {
        player.setMetadata(DOUBLE_DROPS_METADATA_KEY,
                new FixedMetadataValue(getPlugin(), true));

        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            if (player.hasMetadata(DOUBLE_DROPS_METADATA_KEY)) {
                player.removeMetadata(DOUBLE_DROPS_METADATA_KEY, getPlugin());
            }
        }, DOUBLE_DROPS_DURATION_TICKS * level);

        Utils.startTimer(getPlugin(), player,
                DOUBLE_DROPS_DURATION_SECONDS * level,
                "Double drops have ended!");
    }

    /**
     * Right-click: Gives player haste 2 for 30 times the level of the gem.
     *
     * @param player the player who rights-clicked
     * @param level  the level of the gem
     */
    @Override
    protected void rightClick(final Player player, final int level) {
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.FAST_DIGGING,
                HASTE_DURATION_SECONDS * level * RIGHT_CLICK_POTION_EFFECT_TIME,
                1,
                true,
                false));
    }

    /**
     * Shift-click: halves all trade prices for 60 seconds times the level
     * of the gem.
     *
     * @param player the player who rights-clicked
     * @param level  the level of the gem
     */
    @Override
    protected void shiftClick(final Player player, final int level) {
        player.setMetadata(DISCOUNT_METADATA_KEY,
                new FixedMetadataValue(getPlugin(), true));

        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            if (player.hasMetadata(DISCOUNT_METADATA_KEY)) {
                player.removeMetadata(DISCOUNT_METADATA_KEY, getPlugin());
            }
        }, DISCOUNT_DURATION_TICKS * level);

        Utils.startTimer(getPlugin(), player, DISCOUNT_DURATION_SECONDS * level,
                "Trade prices have returned to normal!");
    }

    /**
     * Returns the default lore for the gem.
     *
     * @return A list of strings representing the gem's lore
     */
    @Override
    public final ArrayList<String> getDefaultLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Level %level%");
        lore.add(ChatColor.GREEN + "Abilities");
        lore.add(ChatColor.WHITE + "Right click: Mine blocks faster");
        lore.add(ChatColor.WHITE + "Shift click: Halve all trade prices");
        lore.add(ChatColor.WHITE + "Left click: Double drop rates");
        return lore;
    }

    /**
     * Returns the default effect level for the gem.
     *
     * @return The default effect level
     */
    @Override
    public final int getDefaultEffectLevel() {
        return 1;
    }

    /**
     * Returns the default effect type for the gem.
     *
     * @return The default potion effect type
     */
    @Override
    public final PotionEffectType getDefaultEffectType() {
        return PotionEffectType.HERO_OF_THE_VILLAGE;
    }
}
