package master.gems;

import dev.iseal.powergems.misc.AbstractClasses.Gem;
import master.Keys;
import master.MPG;

import static dev.iseal.sealLib.SealLib.getPlugin;

import java.util.ArrayList;
import java.util.Objects;

import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;


public class WitherGem extends Gem {

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
                witherSkull.setCharged(true);
                witherSkull.getPersistentDataContainer().set(Keys.WITHER_SKULL, PersistentDataType.BYTE, (byte) 1);
                witherSkull.getPersistentDataContainer().set(Keys.WITHER_SKULL_LEVEL, PersistentDataType.INTEGER, level);
            }, (long) i * delay);
        }
    }

    /** Applies temporary damage reduction; duration scales with level. */
    @Override
    protected void rightClick(Player player, int level) {
        player.getPersistentDataContainer().set(Keys.WITHER_DAMAGE_REDUCTION, PersistentDataType.BYTE, (byte) 1);
        player.sendMessage(Component.text("You are now immune to projectiles and take reduced damage for " +
                (10 + (2 * Math.max(1, level))) + " seconds!", NamedTextColor.BLACK));
        int durationTicks = (10 + (2 * Math.max(1, level))) * 20;

        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            if (player.getPersistentDataContainer().has(Keys.WITHER_DAMAGE_REDUCTION, PersistentDataType.BYTE)) {
                player.getPersistentDataContainer().remove(Keys.WITHER_DAMAGE_REDUCTION);
                player.sendMessage(Component.text("Your damage reduction has worn off.", NamedTextColor.BLACK));
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
        lore.add("<dark_gray>Level <level>");
        lore.add("<dark_gray>Abilities");
        lore.add("<white>Right click: Reduce damage for 50% against all attacks, and 100% against projectiles");
        lore.add("<white>Shift click: Create explosion and give everyone around you glowing effect");
        lore.add("<white>Left click: Launch wither skulls at your target");
        if(MPG.PassiveLoreEnabled) {lore.add("<aqua>Passive: Regeneration");}
        return lore;
    }

    @Override
    public PotionEffectType getDefaultEffectType() {
        return PotionEffectType.REGENERATION;
    }

    @Override
    public int getDefaultEffectLevel() {
        return 1;
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
