package master.gems;

import dev.iseal.powergems.misc.AbstractClasses.Gem;
import org.bukkit.ChatColor;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class PoisonGem extends Gem {
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
                    20 * level,
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
        Arrow arrow = player.launchProjectile(Arrow.class);
        arrow.setPersistent(true);
        arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);

        PotionEffect effect = usePoison ?
            new PotionEffect(PotionEffectType.POISON, 100, 1, false, true) :
            new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 1, 1, false, true);

        arrow.addCustomEffect(effect, true);

        // Scale velocity with gem level
        arrow.setVelocity(arrow.getVelocity().multiply(1 + (level * 0.1)));
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
        player.sendMessage(ChatColor.DARK_GREEN + "Removed all negative potion effects!");
    }

    /** Provides the default lore lines. */
    @Override
    public ArrayList<String> getDefaultLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GREEN + "Level %level%");
        lore.add(ChatColor.DARK_GREEN + "Abilities");
        lore.add(ChatColor.WHITE + "Right click: Shoot a tipped arrow with poison or instant damage.");
        lore.add(ChatColor.WHITE + "Shift click: Remove all negative potion effects from yourself.");
        lore.add(ChatColor.WHITE + "Left click: Apply regeneration to yourself and poison to players you look at.");
        return lore;
    }

    @Override
    public int getDefaultEffectLevel() { return 1; }

    @Override
    public PotionEffectType getDefaultEffectType() { return PotionEffectType.REGENERATION; }

    @Override
    public Particle getDefaultParticle() {
        return Particle.WITCH;
    }

    @Override
    public BlockData getParticleBlockData() {
        return null;
    }
}
