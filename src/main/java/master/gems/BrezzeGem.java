package master.gems;

import dev.iseal.powergems.misc.AbstractClasses.Gem;
import master.MPG;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

import static dev.iseal.sealLib.SealLib.getPlugin;

public class BrezzeGem extends Gem {
    public BrezzeGem() {
        super("Brezze");
    }

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    /** Launches multiple wind charges that knockback entities. */
    @Override
    protected void rightClick(Player player, int level) {
        int totalCharges = 5 + level;

        for(int i = 0; i < totalCharges; i++) {
            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                if (player.isOnline() && !player.isDead()) {
                    WindCharge windCharge = player.launchProjectile(WindCharge.class);
                    windCharge.setGlowing(true);
                }
            },  3L * i);
        }
    }

    /** Dash forward in the direction you're facing. */
    @Override
    protected void leftClick(Player player, int level) {
        Location location = player.getLocation();
        player.setVelocity(location.getDirection().multiply(5));
        player.spawnParticle(Particle.CLOUD, location, 200, 1, 1, 1, 0.3);
    }

    /** Summons breeze mobs to fight for you. */
    @Override
    protected void shiftClick(Player player, int level) {
        Location spawnLocation = player.getLocation().add(0, 1, 0);
        int spawnCount = 2 + (level / 2); // Scale with level
        for (int a = 0; a < spawnCount; a++) {
            player.getWorld().spawn(spawnLocation, Breeze.class, breeze -> {
                breeze.setPersistent(true);
                breeze.setRemoveWhenFarAway(true);
                breeze.setGlowing(true);
                var nearby = player.getNearbyEntities(20, 20, 20);
                for (var entity : nearby) {
                    if (entity instanceof Monster ||
                        (entity instanceof Player p && p != player)) {
                        breeze.setTarget((LivingEntity) entity);
                        break;
                    }
                }
            });
        }
    }

    @Override
    public ArrayList<String> getDefaultLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add("<gradient:#AAFFFF:#00AABB>Level <level></gradient>");
        lore.add("<gradient:#AAFFFF:#00AABB>Abilities</gradient>");
        lore.add("<white>Left-Click: Dash Forward</white>");
        lore.add("<white>Right-Click: Launch Wind Charges</white>");
        lore.add("<white>Shift-Click: Summon Breezes</white>");
        if(MPG.PassiveLoreEnabled) {lore.add("<aqua>Passive: No fall damage</aqua>");}
        return lore;
    }

    @Override
    public PotionEffectType getDefaultEffectType() {
        return PotionEffectType.LUCK;
    }

    @Override
    public int getDefaultEffectLevel() {
        return 0;
    }

    @Override
    public Particle getDefaultParticle() {
        return Particle.CLOUD;
    }

    @Override
    public BlockData getParticleBlockData() {
        return null;
    }
}
