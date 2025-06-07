package Master.gems;

import Master.Utils.Utils;
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

public class AffluenceGem extends Gem {

    public final static String DISCOUNT_METADATA_KEY = "HALVE_PRICE";
    public final static String DOUBLE_DROPS_METADATA_KEY = "DOUBLE_DROPS";

    public AffluenceGem() {
        super("Affluence");
    }

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    /**
     * Left-click: Gives player double drops for 20-second times the level of the gem.
     *
     * @param player the player who left-clicked
     * @param level  the level of the gem
     */
    @Override
    protected void leftClick(Player player, int level) {
        player.setMetadata(DOUBLE_DROPS_METADATA_KEY, new FixedMetadataValue(getPlugin(), true));

        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            if (player.hasMetadata(DOUBLE_DROPS_METADATA_KEY)) {
                player.removeMetadata(DOUBLE_DROPS_METADATA_KEY, getPlugin());
            }
        }, 400L * level);
        Utils.startTimer(getPlugin(), player, 20 * level, "Double drops have ended!");
    } // 60 seconds per level

    /**
     * Right-click: Gives player haste 2 for 30 times the level of the gem.
     *
     * @param player the player who rights-clicked
     * @param level  the level of the gem
     */
    @Override
    protected void rightClick(Player player, int level) {
        plr.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING,
                30 * level,
                2,
                true,
                false));
    }

    /**
     * Shift-click: halves all trade prices for 60 seconds times the level of the gem.
     *
     * @param player the player who rights-clicked
     * @param level  the level of the gem
     */
    @Override
    protected void shiftClick(Player player, int level) {

        player.setMetadata(DISCOUNT_METADATA_KEY, new FixedMetadataValue(getPlugin(), true));

        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            if (player.hasMetadata(DISCOUNT_METADATA_KEY)) {
                player.removeMetadata(DISCOUNT_METADATA_KEY, getPlugin());
            }
        }, 1200L * level); // 60 seconds per level
        Utils.startTimer(getPlugin(), player, 60 * level, "Trade prices have returned to normal!");
    }

    @Override
    public ArrayList<String> getDefaultLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Level %level%");
        lore.add(ChatColor.GREEN + "Abilities");
        lore.add(ChatColor.WHITE
                + "Right click: Mine blocks faster");
        lore.add(ChatColor.WHITE
                + "Shift click: Halve all trade prices");
        lore.add(ChatColor.WHITE
                + "Left click: Double drop rates");
        return lore;
    }

    @Override
    public int getDefaultEffectLevel() {
        return 1;
    }

    @Override
    public PotionEffectType getDefaultEffectType() {
        return PotionEffectType.HERO_OF_THE_VILLAGE;
    }
}
