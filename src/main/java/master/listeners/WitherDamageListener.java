package master.listeners;

import master.Keys;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataType;

public class WitherDamageListener implements Listener {
    /**
     * Reduces damage taken by players who have Wither Gem protection by 50%.
     */
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if(e.getEntity() instanceof Player plr && plr.getPersistentDataContainer().has(Keys.WITHER_DAMAGE_REDUCTION, PersistentDataType.BYTE)) {
            double originalDamage = e.getDamage();
            double reducedDamage = originalDamage * 0.5;
            e.setDamage(reducedDamage);
        }
    }

    /**
     * Cancels projectile damage for players with Wither Gem protection.
     */
    @EventHandler
    public void onProjectileDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player plr &&
                plr.getPersistentDataContainer().has(Keys.WITHER_DAMAGE_REDUCTION, PersistentDataType.BYTE) &&
                e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            e.setCancelled(true);
        }
    }

    /**
     * Increases damage dealt by Wither Skulls launched from players with the Wither Gem.
     */
    @EventHandler
    public void onWitherSkullDamage(EntityDamageByEntityEvent e) {
        if(e.getDamager() instanceof WitherSkull skull && skull.getPersistentDataContainer().has(Keys.WITHER_SKULL, PersistentDataType.BYTE)) {
            int level = skull.getPersistentDataContainer().getOrDefault(Keys.WITHER_SKULL_LEVEL, PersistentDataType.INTEGER, 1);
            double originalDamage = e.getDamage();
            double buffedDamage = originalDamage + (3.0 * level);
            e.setDamage(buffedDamage);
        }
    }
}