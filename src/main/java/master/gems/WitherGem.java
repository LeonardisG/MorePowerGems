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

import net.kyori.adventure.text.minimessage.MiniMessage;


public class WitherGem extends Gem {
    private static final MiniMessage MM = MiniMessage.miniMessage();

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
        player.sendMessage(MM.deserialize("<black>You are now immune to projectiles and take reduced damage for " +
                (10 + (2 * Math.max(1, level))) + " seconds!"));
        int durationTicks = (10 + (2 * Math.max(1, level))) * 20;

        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            if (player.getPersistentDataContainer().has(Keys.WITHER_DAMAGE_REDUCTION, PersistentDataType.BYTE)) {
                player.getPersistentDataContainer().remove(Keys.WITHER_DAMAGE_REDUCTION);
                player.sendMessage(MM.deserialize("<black>Your damage reduction has worn off."));
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
        lore.add("<gradient:#888888:#111111>Level <level></gradient>");
        lore.add("<gradient:#888888:#111111>Abilities</gradient>");
        lore.add("<white>Right click: Reduce damage for 50% against all attacks, and 100% against projectiles</white>");
        lore.add("<white>Shift click: Create explosion and give everyone around you glowing effect</white>");
        lore.add("<white>Left click: Launch wither skulls at your target</white>");
        if(MPG.PassiveLoreEnabled) {lore.add("<aqua>Passive: Regeneration</aqua>");}
        return lore;
    }

    @Override
    public PotionEffectType getDefaultEffectType() {
        return PotionEffectType.REGENERATION;
    }

    @Override
    public int getDefaultEffectLevel() {
        return 0;
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
