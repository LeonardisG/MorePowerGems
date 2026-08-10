package master.gems;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import dev.iseal.powergems.misc.AbstractClasses.Gem;
import master.MPG;
import net.kyori.adventure.text.minimessage.MiniMessage;


public class PoisonGem extends Gem {
    private static final MiniMessage MM = MiniMessage.miniMessage();
    public PoisonGem() { super("Poison"); }

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    /** Regen self; poison aimed player in FOV. */
    @Override
    protected void leftClick(Player player, int level) {
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.REGENERATION,
                200 * level,
                2,
                true,
                false
        ));
        int maxDistance = 15 + (level * 2);
        List<Entity> nearbyEntities = player.getNearbyEntities(maxDistance, maxDistance, maxDistance);
        Player targetPlayer = null;
        double closestAngle = 0.8; // ~35 deg
        for (Entity entity : nearbyEntities) {
            if (entity instanceof Player && entity != player) {
                Vector playerDirection = player.getEyeLocation().getDirection().normalize();
                Vector toTarget = entity.getLocation().toVector().subtract(player.getEyeLocation().toVector()).normalize();
                double dot = toTarget.dot(playerDirection);
                if (dot > closestAngle) {
                    targetPlayer = (Player) entity;
                    closestAngle = dot;
                }
            }
        }
        if (targetPlayer != null) {
            targetPlayer.addPotionEffect(new PotionEffect(
                    PotionEffectType.POISON,
                    60 * level,
                    1,
                    true,
                    true
            ));
        }
    }

    /** Fires arrow with poison or instant damage effects. */
    @Override
    protected void rightClick(Player player, int level) {
        boolean usePoison = ThreadLocalRandom.current().nextBoolean();
        PotionEffect effect = usePoison ?
                new PotionEffect(PotionEffectType.POISON, 100, 1, false, true) :
                new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 1, 1, false, true);

        int ringArrows = Math.max(1, level);
        double radius = 1.0 + (level * 0.2);
        double convergeDistance = 12.0;
        float speed = 1.5f + level * 0.1f;

        Location eye = player.getEyeLocation();
        Vector baseDir = eye.getDirection().normalize();

        // Orthonormal frame around the look direction. Looking straight up/down makes
        // baseDir parallel to world up, so pick a different reference axis there.
        Vector reference = Math.abs(baseDir.getY()) > 0.999 ? new Vector(1, 0, 0) : new Vector(0, 1, 0);
        Vector right = baseDir.getCrossProduct(reference).normalize();
        Vector up = right.getCrossProduct(baseDir).normalize();

        // The ring arrows aim at this point instead of flying parallel, so the spread
        // actually closes back onto the crosshair instead of passing either side of it.
        Vector focus = eye.toVector().add(baseDir.clone().multiply(convergeDistance));

        Arrow central = player.getWorld().spawnArrow(eye, baseDir, speed, 0f);
        central.setShooter(player);
        central.setPersistent(true);
        central.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        central.addCustomEffect(effect, true);

        for (int i = 0; i < ringArrows; i++) {
            double angle = 2 * Math.PI * i / ringArrows;
            Vector offset = right.clone().multiply(Math.cos(angle) * radius)
                    .add(up.clone().multiply(Math.sin(angle) * radius));

            Location spawn = eye.clone().add(offset);
            // A ring position inside a wall would stick the arrow on spawn; fall back to the eye.
            if (!spawn.getBlock().isPassable()) {
                spawn = eye.clone();
            }
            Vector dir = focus.clone().subtract(spawn.toVector()).normalize();

            Arrow arrow = player.getWorld().spawnArrow(spawn, dir, speed, 0f);
            arrow.setShooter(player);
            arrow.setPersistent(true);
            arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
            arrow.addCustomEffect(effect, true);
        }
    }

    /** Cleanses negative potion effects from self. */
    @Override
    protected void shiftClick(Player player, int level) {
        List<PotionEffectType> badEffects = new ArrayList<>();
        badEffects.add(PotionEffectType.POISON);
        badEffects.add(PotionEffectType.WITHER);
        badEffects.add(PotionEffectType.BLINDNESS);
        badEffects.add(PotionEffectType.SLOWNESS);
        badEffects.add(PotionEffectType.MINING_FATIGUE);
        badEffects.add(PotionEffectType.NAUSEA);
        badEffects.add(PotionEffectType.HUNGER);
        badEffects.add(PotionEffectType.WEAKNESS);
        badEffects.add(PotionEffectType.UNLUCK);
        badEffects.add(PotionEffectType.BAD_OMEN);
        badEffects.add(PotionEffectType.DARKNESS);
        badEffects.add(PotionEffectType.LEVITATION);
        for (PotionEffectType effectType : badEffects) {
            if (player.hasPotionEffect(effectType)) {
                player.removePotionEffect(effectType);
            }
        }
        player.sendMessage(MM.deserialize("<dark_green>Removed all negative potion effects!"));
    }

    /** Provides the default lore lines. */
    @Override
    public ArrayList<String> getDefaultLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add("<gradient:#55FF55:#005500>Level <level></gradient>");
        lore.add("<gradient:#55FF55:#005500>Abilities</gradient>");
        lore.add("<white>Right click: Shoot a tipped arrow with poison or instant damage.</white>");
        lore.add("<white>Shift click: Remove all negative potion effects from yourself.</white>");
        lore.add("<white>Left click: Apply regeneration to yourself and poison to players you look at.</white>");
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
        return Particle.WITCH;
    }

    @Override
    public BlockData getParticleBlockData() {
        return null;
    }
}
