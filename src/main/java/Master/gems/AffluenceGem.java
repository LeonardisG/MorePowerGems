package Master.gems;

import dev.iseal.powergems.misc.AbstractClasses.Gem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class AffluenceGem extends Gem {

    public AffluenceGem() {
        super("Affluence");
    }
    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }
    @Override
    protected void leftClick(Player player, int level) {

    }

    /**
     * Right-click: Gives player haste 2 for 30 times the level of the gem.
     * @param player the player who rights-clicked
     * @param level the level of the gem
     */
    @Override
    protected void rightClick(Player player, int level) {
        plr.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING,
                30 * level,
                2,
                true,
                false));
    }

    @Override
    protected void shiftClick(Player player, int level) {

    }

    @Override
    public ArrayList<String> getDefaultLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Level %level%");
        lore.add(ChatColor.GREEN + "Abilities");
        lore.add(ChatColor.WHITE
                + "Right click: .");
        lore.add(ChatColor.WHITE
                + "Shift click: .");
        lore.add(ChatColor.WHITE
                + "Left click: .");
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
