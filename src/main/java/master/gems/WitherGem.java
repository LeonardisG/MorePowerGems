package master.gems;

import dev.iseal.powergems.misc.AbstractClasses.Gem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Objects;

import static dev.iseal.sealLib.SealLib.getPlugin;

public class WitherGem extends Gem {
    /**
     * Metadata key for wither damage reduction effect.
     */
    public static final String WITHER_DAMAGE_REDUCTION_KEY = "WITHER_DAMAGE_REDUCTION";

    /**
     * Constructs the WitherGem
     */
    public WitherGem() {
        super("Wither");
    }
    /**
     * Processes the player's action
     *
     * @param act  the action performed
     * @param plr  the player using the gem
     * @param item the gem item
     */
    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    /**
     * Launches Wither Skulls when the gem is left-clicked.
     * <p>
     * For each level of the gem, it launches an additional Wither Skull.
     *
     * @param player the player who left-clicked
     * @param level  the level of the gem which affects the number of skulls
     */
    @Override
    protected void leftClick(Player player, int level) {
        int delay = 5; // 0.25-second delay between skulls
        for (int i = 0; i < level; i++) {
            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                WitherSkull witherSkull = player.launchProjectile(WitherSkull.class);
                witherSkull.setGlowing(true);
                witherSkull.setShooter(player);
            }, (long) i * delay);
        }
    }

    /**
     * Applies temporary damage reduction when the gem is right-clicked.
     * <p>
     * This method applies the Wither damage reduction effect to the player
     * for a duration of 10 seconds plus 2 seconds per gem level.
     *
     * @param player the player who rights-clicked
     * @param level the level of the gem
     */
    @Override
    protected void rightClick(Player player, int level) {
        player.setMetadata(WITHER_DAMAGE_REDUCTION_KEY,
                new FixedMetadataValue(getPlugin(), true));

        // Calculate duration: 10 seconds + 2 seconds per level
        // Converting to ticks (20 ticks = 1 second)
        int durationTicks = (10 + (2 * level)) * 20;

        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            if (player.hasMetadata(WITHER_DAMAGE_REDUCTION_KEY)) {
                player.removeMetadata(WITHER_DAMAGE_REDUCTION_KEY, getPlugin());
            }
        }, durationTicks);
    }

    /**
     * Creates an explosion at the player's location.
     * <p>
     * The explosion power increases with the gem level.
     * Does not affect the gem user.
     * @param player the player who shifts-clicked
     * @param level  the level of the gem which affects the explosion power
     */
    @Override
    protected void shiftClick(Player player, int level) {
        Location loc = player.getLocation();
        loc.add(0, 1, 0);
        Objects.requireNonNull(loc.getWorld()).createExplosion(loc, 2.0F + level, true, true, player);
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
        return null;
    }
}