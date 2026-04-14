package master.gems;

import dev.iseal.powergems.misc.AbstractClasses.Gem;
import master.Keys;
import master.MPG;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;

import static dev.iseal.sealLib.SealLib.getPlugin;

public class AffluenceGem extends Gem {


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
        player.getPersistentDataContainer().set(Keys.AFFLUENCE_DOUBLE_DROPS, PersistentDataType.BYTE, (byte) 1);
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            if (player.getPersistentDataContainer().has(Keys.AFFLUENCE_DOUBLE_DROPS, PersistentDataType.BYTE)) {
                player.getPersistentDataContainer().remove(Keys.AFFLUENCE_DOUBLE_DROPS);
            }
        }, 400L * level);
        player.sendMessage(Component.text("Double drops activated for "
                + (20 * level) + " seconds!", NamedTextColor.GREEN));
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
        player.sendMessage(Component.text("Haste activated for "
                + (30 * level) + " seconds!", NamedTextColor.GREEN));
    }

    /** Halves villager trade prices temporarily. */
    @Override
    protected void shiftClick(final Player player, int level) {
        player.getPersistentDataContainer().set(Keys.AFFLUENCE_DISCOUNT, PersistentDataType.BYTE, (byte) 1);
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            if (player.getPersistentDataContainer().has(Keys.AFFLUENCE_DISCOUNT, PersistentDataType.BYTE)) {
                player.getPersistentDataContainer().remove(Keys.AFFLUENCE_DISCOUNT);
            }
        }, 1200L * level);
        player.sendMessage(Component.text("Trade prices halved for "
                + (60 * level) + " seconds!", NamedTextColor.GREEN));
    }

    @Override
    public ArrayList<String> getDefaultLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§aLevel %level%");
        lore.add("§aAbilities");
        lore.add("§fRight click: Mine blocks faster");
        lore.add("§fShift click: Halve all trade prices");
        lore.add("§fLeft click: Double drop rates");
        if(MPG.PassiveLoreEnabled) {lore.add("§bPassive: Hero of the Village");}
        return lore;
    }

    @Override
    public PotionEffectType getDefaultEffectType() {
        return PotionEffectType.HERO_OF_THE_VILLAGE;
    }

    @Override
    public int getDefaultEffectLevel() {
        return 1;
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
