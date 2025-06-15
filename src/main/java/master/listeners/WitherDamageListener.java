package master.listeners;

import master.gems.WitherGem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Listener class that handles damage modifications related to the Wither Gem.
 * <p>
 * This class implements two damage modification behaviors:
 * <ul>
 *   <li>Reducing incoming damage for players with Wither Gem protection</li>
 *   <li>Cancelling projectile damage from players with Wither Gem protection</li>
 * </ul>
 */
public class WitherDamageListener implements Listener {

    /**
     * Reduces damage taken by players who have Wither Gem protection.
     * <p>
     * When a player with the Wither damage reduction metadata receives damage,
     * this handler reduces the incoming damage by 50%.
     *
     * @param e The entity damage event
     */
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if(e.getEntity() instanceof Player plr && plr.hasMetadata(WitherGem.WITHER_DAMAGE_REDUCTION_KEY)) {
            double originalDamage = e.getDamage();
            double reducedDamage = originalDamage * 0.5; // Reduce damage by 50%

            e.setDamage(reducedDamage);
        }
    }

    /**
     * Cancels projectile damage from players with Wither Gem protection.
     * <p>
     * When a player with the Wither damage reduction metadata attacks with
     * a projectile, this handler completely cancels the damage event.
     *
     * @param e The entity damage event
     */
    @EventHandler
    public void onProjectileDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player plr &&
                plr.hasMetadata(WitherGem.WITHER_DAMAGE_REDUCTION_KEY) &&
                e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            e.setCancelled(true); // Cancel the damage from projectiles
        }
    }
}