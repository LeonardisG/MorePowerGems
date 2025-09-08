package master.listeners;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import master.gems.WitherGem;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class WitherDamageListener implements Listener {
GemManager gm = SingletonManager.getInstance().gemManager;
    /**
     * Reduces damage taken by players who have Wither Gem protection by 50%.
     */
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if(e.getEntity() instanceof Player plr && plr.hasMetadata(WitherGem.WITHER_DAMAGE_REDUCTION_KEY)) {
            double originalDamage = e.getDamage();
            double reducedDamage = originalDamage * 0.5; // Reduce damage by 50%

            e.setDamage(reducedDamage);
        }}

    /**
     * Cancels projectile damage from players with Wither Gem protection.
     */
    @EventHandler
    public void onProjectileDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player plr &&
                plr.hasMetadata(WitherGem.WITHER_DAMAGE_REDUCTION_KEY) &&
                e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            e.setCancelled(true); // Cancel the damage from projectiles
        }
    }

    /**
     * Increases damage dealt by Wither Skulls launched from players with the Wither Gem.
     */
    @EventHandler
    public void onWitherSkullDamage(EntityDamageByEntityEvent e) {
        if(e.getDamager() instanceof WitherSkull skull && skull.hasMetadata(WitherGem.WITHER_SKULL_KEY)) {

            int level = skull.getMetadata(WitherGem.WITHER_SKULL_LEVEL_KEY).getFirst().asInt();

            double originalDamage = e.getDamage();
            double buffedDamage = originalDamage + (3.0 * level);
            e.setDamage(buffedDamage);
        }
    }
}