package master.gems;

import dev.iseal.powergems.misc.AbstractClasses.Gem;
import master.Keys;
import master.MPG;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

import static dev.iseal.sealLib.SealLib.getPlugin;

public class AffluenceGem extends Gem {


    public AffluenceGem() {
        super("Affluence");
    }

    private static final MiniMessage MM = MiniMessage.miniMessage();

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
        player.sendMessage(MM.deserialize("<green>Double drops activated for "
                + (20 * level) + " seconds!"));
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
        player.sendMessage(MM.deserialize("<green>Haste activated for "
                + (30 * level) + " seconds!"));
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
        player.sendMessage(MM.deserialize("<green>Trade prices halved for "
                + (60 * level) + " seconds!"));
    }

    @Override
    public ArrayList<String> getDefaultLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add("<gradient:#AAFFAA:#00AA44>Level <level></gradient>");
        lore.add("<gradient:#AAFFAA:#00AA44>Abilities</gradient>");
        lore.add("<white>Right click: Mine blocks faster</white>");
        lore.add("<white>Shift click: Halve all trade prices</white>");
        lore.add("<white>Left click: Double drop rates</white>");
        if(MPG.PassiveLoreEnabled) {lore.add("<aqua>Passive: Hero of the Village</aqua>");}
        return lore;
    }

    @Override
    public PotionEffectType getDefaultEffectType() {
        return PotionEffectType.HERO_OF_THE_VILLAGE;
    }

    @Override
    public int getDefaultEffectLevel() {
        return 0;
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
