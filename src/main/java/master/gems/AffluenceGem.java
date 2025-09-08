package master.gems;

import dev.iseal.powergems.misc.AbstractClasses.Gem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

import static dev.iseal.sealLib.SealLib.getPlugin;

public class AffluenceGem extends Gem {

    public static final String DISCOUNT_METADATA_KEY = "HALVE_PRICE";
    public static final String DOUBLE_DROPS_METADATA_KEY = "DOUBLE_DROPS";

    public AffluenceGem() {
        super("Affluence");
    }

    @Override
    public void call(final Action act, final Player plr, final ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    /** Activates temporary double drop metadata. */
    @Override
    protected void leftClick(Player player, int level) {
        player.setMetadata(DOUBLE_DROPS_METADATA_KEY, new FixedMetadataValue(getPlugin(), true));
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            if (player.hasMetadata(DOUBLE_DROPS_METADATA_KEY)) {
                player.removeMetadata(DOUBLE_DROPS_METADATA_KEY, getPlugin());
            }
        }, 400L * level);
        player.sendMessage(ChatColor.GREEN + "Double drops activated for " + (20 * level) + " seconds!");
    }

    /** Grants a haste effect for faster mining. */
    @Override
    protected void rightClick(final Player player, int level) {
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.HASTE,
                30 * level * 20,
                1,
                true,
                false));
        player.sendMessage(ChatColor.GREEN + "Haste activated for " + (30 * level) + " seconds!");
    }

    /** Halves villager trade prices temporarily. */
    @Override
    protected void shiftClick(final Player player, int level) {
        player.setMetadata(DISCOUNT_METADATA_KEY, new FixedMetadataValue(getPlugin(), true));
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            if (player.hasMetadata(DISCOUNT_METADATA_KEY)) {
                player.removeMetadata(DISCOUNT_METADATA_KEY, getPlugin());
            }
        }, 1200L * level);
        player.sendMessage(ChatColor.GREEN + "Trade prices halved for " + (60 * level) + " seconds!");
    }

    @Override
    public ArrayList<String> getDefaultLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Level %level%");
        lore.add(ChatColor.GREEN + "Abilities");
        lore.add(ChatColor.WHITE + "Right click: Mine blocks faster");
        lore.add(ChatColor.WHITE + "Shift click: Halve all trade prices");
        lore.add(ChatColor.WHITE + "Left click: Double drop rates");
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

    @Override
    public Particle getDefaultParticle() {
        return Particle.HAPPY_VILLAGER;
    }

    @Override
    public BlockData getParticleBlockData() {
        return null;
    }
}
