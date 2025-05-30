package Master.gems;

import dev.iseal.powergems.misc.AbstractClasses.Gem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class PoisonGem extends Gem {

    public PoisonGem() {
        super("Poison");
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
        return 0;
    }

    @Override
    public PotionEffectType getDefaultEffectType() {
        return null;
    }
}
