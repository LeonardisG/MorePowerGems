package Master.gems;

import dev.iseal.powergems.misc.AbstractClasses.Gem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class PoisonGem extends Gem {

    public PoisonGem() {
        super("Poison");
    }
    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    /** Applies regeneration to the user player and position to the player he is looking at.
     * @param player the player who left-clicked
     * @param level the level of the gem
     */
    @Override
    protected void leftClick(Player player, int level) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,
                10 * level,
                1,
                true,
                false
        ));

        int maxDistance = 15 + (level * 2); // Increase max distance based on gem level
        List<Entity> nearbyEntities = player.getNearbyEntities(maxDistance, maxDistance, maxDistance);

        Player targetPlayer = null;
        double closestAngle = 0.8; // Approximately 35 degree field of view

        for(Entity entity : nearbyEntities) {
            if(entity instanceof Player && entity != player) {
                Vector playerDirection = player.getEyeLocation().getDirection().normalize();
                Vector toTarget = entity.getLocation().toVector().subtract(player.getEyeLocation().toVector()).normalize();

                double dot = toTarget.dot(playerDirection);

                if(dot > closestAngle) {
                    targetPlayer = (Player)entity;
                    closestAngle = dot;
                }
            }
        }

        // Apply poison effect to the targeted player if found
        if(targetPlayer != null) {
            targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.POISON,
                    20 * level,// Duration scales with gem level
                    1,
                    true,
                    true
            ));
        }
    }

    /** Shoots a tipped arrow, its 50%-50% chance to be either a poison or instant damage(harming) arrow.
     * @param player the player
     * @param level the level of the gem
     */
    @Override
    protected void rightClick(Player player, int level) {
        Random random = new Random();
        boolean usePoison = random.nextBoolean(); // If true, use poison; if false, use instant damage

        Arrow arrow = player.launchProjectile(Arrow.class);
        arrow.setPersistent(true);

        if (usePoison) {
            // Poison arrow
            arrow.setBasePotionData(new PotionData(PotionType.POISON, false, true));
        } else {
            // Instant damage arrow
            arrow.setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE, false, true));
        }

        // Scale velocity with gem level
        arrow.setVelocity(arrow.getVelocity().multiply(1 + (level * 0.1)));
    }


    /** Removes all negative potion effects from the player.
     * @param player the player
     * @param level the level of the gem
     */
    @Override
    protected void shiftClick(Player player, int level) {
        List<PotionEffectType> badEffects = new ArrayList<>();
        badEffects.add(PotionEffectType.POISON);
        badEffects.add(PotionEffectType.HARM);
        badEffects.add(PotionEffectType.WITHER);
        badEffects.add(PotionEffectType.BLINDNESS);
        badEffects.add(PotionEffectType.SLOW);
        badEffects.add(PotionEffectType.SLOW_DIGGING);
        badEffects.add(PotionEffectType.CONFUSION);
        badEffects.add(PotionEffectType.HUNGER);
        badEffects.add(PotionEffectType.WEAKNESS);
        badEffects.add(PotionEffectType.UNLUCK);
        badEffects.add(PotionEffectType.BAD_OMEN);
        badEffects.add(PotionEffectType.DARKNESS);
        badEffects.add(PotionEffectType.LEVITATION);

        for(PotionEffectType effectType : badEffects) {
            if (player.hasPotionEffect(effectType)) {
                player.removePotionEffect(effectType);
            }
        }
    }

    @Override
    public ArrayList<String> getDefaultLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Level %level%");
        lore.add(ChatColor.GREEN + "Abilities");
        lore.add(ChatColor.WHITE +
                "Right click: Shoot a tipped arrow with poison or instant damage.");
        lore.add(ChatColor.WHITE +
                "Shift click: Remove all negative potion effects from yourself.");
        lore.add(ChatColor.WHITE +
                "Left click: Apply regeneration to yourself and poison to players you look at.");
        return lore;
    }

    @Override
    public int getDefaultEffectLevel() {
        return 1;
    }

    @Override
    public PotionEffectType getDefaultEffectType() {
        return PotionEffectType.REGENERATION;
    }
}
