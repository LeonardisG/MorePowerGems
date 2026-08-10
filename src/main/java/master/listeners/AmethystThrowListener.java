package master.listeners;

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

import master.Keys;
import master.MPG;


public class AmethystThrowListener implements Listener {
    private boolean isNotAmethystProjectile(Snowball s) {
        PersistentDataContainer c = s.getPersistentDataContainer();
        return !c.has(Keys.AMETHYST_PROJECTILE, PersistentDataType.BYTE);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof Snowball s) || isNotAmethystProjectile(s)) return;

        s.getWorld().spawnParticle(Particle.TRIAL_OMEN,
                s.getLocation(), 40,0.2,0.2,0.2,0.1);
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
        if (!(e.getDamager() instanceof Snowball s) || isNotAmethystProjectile(s)) return;

        Integer stored = s.getPersistentDataContainer().get(Keys.AMETHYST_LEVEL, PersistentDataType.INTEGER);
        int level = Math.max(1, stored != null ? stored : 1);

        e.setDamage(damageForLevel(level));
    }

    /**
     * Linear damage curve, so every level works instead of only the ones someone
     * remembered to type out.
     * For reference, a player has 20 health points (10 hearts).
     */
    private double damageForLevel(int level) {
        double damage = MPG.AmethystShardBaseDamage
                + MPG.AmethystShardDamagePerLevel * (level - 1);

        if (Double.isNaN(damage)) {
            return MPG.AmethystShardBaseDamage;
        }
        // Deliberately not Math.clamp: it throws if a config sets max below the floor.
        return Math.max(0.0, Math.min(damage, MPG.AmethystShardMaxDamage));
    }
}
