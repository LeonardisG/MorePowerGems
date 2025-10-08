package master.listeners;

import master.Keys;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;


public class AmethystThrowListener implements Listener {
    private boolean isAmethyst(Snowball s) {
        PersistentDataContainer c = s.getPersistentDataContainer();
        return c.has(Keys.AMETHYST_PROJECTILE, PersistentDataType.BYTE);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof Snowball s) || isAmethyst(s)) return;

        if (e.getHitEntity() != null) {
            s.getWorld().spawnParticle(Particle.CRIT, s.getLocation(), 10, 0.1, 0.1, 0.1, 0.02);
            s.getWorld().playSound(s.getLocation(), Sound.ITEM_TRIDENT_HIT, 1.0f, 1.0f);
            Entity victim = e.getHitEntity();
            victim.getWorld().spawnParticle(Particle.END_ROD, victim.getLocation().add(0, 1, 0), 12, 0.2, 0.3, 0.2, 0.01);
            return;
        }

        if (e.getHitBlock() != null) {
            s.getWorld().spawnParticle(Particle.CRIT, s.getLocation(), 10, 0.1, 0.1, 0.1, 0.02);
            s.getWorld().playSound(s.getLocation(), Sound.ITEM_TRIDENT_HIT_GROUND, 1.0f, 1.0f);
        }
    }

    @EventHandler
    public void onProjectileDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Snowball s) || isAmethyst(s)) return;
        Integer stored = s.getPersistentDataContainer().get(Keys.AMETHYST_LEVEL, PersistentDataType.INTEGER);
        int lvl = stored != null ? stored : 1;
        int clamped = Math.max(1, Math.min(3, lvl));
        double damage = switch (clamped) { // 1->3, 2->4, 3+->5 hearts (3-5 damage points)
            case 1 -> 3.0;
            case 2 -> 4.0;
            default -> 5.0;
        };
        e.setDamage(damage);
    }
}
