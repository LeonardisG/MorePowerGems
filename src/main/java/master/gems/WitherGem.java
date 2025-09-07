package master.gems;

import static dev.iseal.sealLib.SealLib.getPlugin;

import java.util.ArrayList;
import java.util.Objects;

import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;

import dev.iseal.powergems.misc.AbstractClasses.Gem;


public class WitherGem extends Gem {

    public static final String WITHER_DAMAGE_REDUCTION_KEY = "WITHER_DAMAGE_REDUCTION";

    public WitherGem() {
        super("Wither");
    }

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    /** Launches Wither Skulls; amount scales with level. */
    @Override
    protected void leftClick(Player player, int level) {
        int delay = 5; // 0.25-second delay between skulls (5 ticks)
        for (int i = 0; i < Math.max(1, level); i++) {
            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                WitherSkull witherSkull = player.launchProjectile(WitherSkull.class);
                witherSkull.setGlowing(true);
                witherSkull.setShooter(player);
            }, (long) i * delay);
        }
    }

    /** Applies temporary damage reduction; duration scales with level. */
    @Override
    protected void rightClick(Player player, int level) {
        player.setMetadata(WITHER_DAMAGE_REDUCTION_KEY,
                new FixedMetadataValue(getPlugin(), true));
        player.sendMessage(ChatColor.BLACK + "You are now immune to projectiles and take reduced damage for " +
                (10 + (2 * Math.max(1, level))) + " seconds!");
        int durationTicks = (10 + (2 * Math.max(1, level))) * 20;

        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            if (player.hasMetadata(WITHER_DAMAGE_REDUCTION_KEY)) {
                player.removeMetadata(WITHER_DAMAGE_REDUCTION_KEY, getPlugin());
                player.sendMessage(ChatColor.BLACK + "Your damage reduction has worn off.");
            }
        }, durationTicks);
    }



    /** Creates an explosion; power scales with level; doesn't hurt the user. */
    @Override
    protected void shiftClick(Player player, int level) {
        Location loc = player.getLocation();
        loc.add(0, 1, 0);
        Objects.requireNonNull(loc.getWorld()).createExplosion(loc, 2.0F + Math.max(0, level), true, true, player);
    }

    @Override
    public ArrayList<String> getDefaultLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Level %level%");
        lore.add(ChatColor.GREEN + "Abilities");
        lore.add(ChatColor.WHITE
                + "Right click: Reduce damage for 50% against all attacks, and 100% against projectiles");
        lore.add(ChatColor.WHITE
                + "Shift click: Create explosion and give everyone around you glowing effect");
        lore.add(ChatColor.WHITE
                + "Left click: Launch wither skulls at your target");
        return lore;
    }

    @Override
    public int getDefaultEffectLevel() {
        return 1;
    }

    @Override
    public PotionEffectType getDefaultEffectType() {
        return PotionEffectType.REGENERATION;
    }

    @Override
    public Particle getDefaultParticle() {
        return Particle.LARGE_SMOKE;
    }

    @Override
    public BlockData getParticleBlockData() {
        return null;
    }
}
