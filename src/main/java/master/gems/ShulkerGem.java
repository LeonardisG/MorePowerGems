package master.gems;

import dev.iseal.powergems.misc.AbstractClasses.Gem;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;



public class ShulkerGem extends Gem {

    public ShulkerGem() {
        super("Shulker");
    }

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    /** Applies levitation effect to nearby players. */
    @Override
    protected void leftClick(Player player, int level) {
        int radius = 5 + level;
        List<Entity> nearbyEntities = player.getNearbyEntities(radius, radius, radius);

        for (Entity entity : nearbyEntities) {
            if (entity instanceof Player && entity != player) {
                ((Player) entity).addPotionEffect(new PotionEffect(
                        PotionEffectType.LEVITATION,
                        40 * level,
                        1,
                        true,
                        false
                ));
            }
        }

        player.sendMessage(ChatColor.LIGHT_PURPLE + "Applied levitation to nearby players!");
    }

    /** Shoots shulker bullets at nearby enemies. */
    @Override
    protected void rightClick(Player player, int level) {
        int maxDistance = 10 + (level * 3);
        List<Entity> nearbyEntities = player.getNearbyEntities(maxDistance, maxDistance, maxDistance);

        int bulletsShot = 0;
        int maxBullets = Math.min(3 + level, 6);

        for (Entity entity : nearbyEntities) {
            if (bulletsShot < maxBullets) {
                ShulkerBullet bullet = player.getWorld().spawn(
                    player.getEyeLocation().add(player.getEyeLocation().getDirection()),
                    ShulkerBullet.class
                );
                bullet.setTarget(entity);
                bullet.setShooter(player);
                bulletsShot++;
            }
        }
    }

    /** Teleports player to a safe location within range. */
    @Override
    protected void shiftClick(Player player, int level) {
        Vector direction = player.getEyeLocation().getDirection();
        Location targetLocation = player.getLocation().clone();

        int distance = 5 + (level * 2);
        targetLocation.add(direction.multiply(distance));

        int maxSearchDown = 10;
        int searchCount = 0;

        if (targetLocation.getBlock().getType().isSolid()) {
            targetLocation.add(0, 2, 0);
        }

        while (searchCount < maxSearchDown && targetLocation.getY() > targetLocation.getWorld().getMinHeight()) {
            if (targetLocation.getBlock().getType().isSolid()) {
                targetLocation.add(0, 1, 0);
                break;
            }
            targetLocation.subtract(0, 1, 0);
            searchCount++;
        }

        if (targetLocation.getBlock().getType() == Material.AIR &&
            targetLocation.clone().add(0, 1, 0).getBlock().getType() == Material.AIR &&
            targetLocation.clone().subtract(0, 1, 0).getBlock().getType().isSolid()) {

            // Spawn particles at old location
            player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 20);

            player.teleport(targetLocation);

            // Spawn particles at new location
            player.getWorld().spawnParticle(Particle.PORTAL, targetLocation, 20);

            player.sendMessage(ChatColor.LIGHT_PURPLE + "Teleported");
        } else {
            player.sendMessage(ChatColor.RED + "Cannot teleport, unsafe location!");
        }
    }

    @Override
    public ArrayList<String> getDefaultLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "Level %level%");
        lore.add(ChatColor.LIGHT_PURPLE + "Abilities");
        lore.add(ChatColor.WHITE + "Left click: Apply levitation to nearby players");
        lore.add(ChatColor.WHITE + "Right click: Shoot shulker bullets");
        lore.add(ChatColor.WHITE + "Shift click: Teleport forward");
        return lore;
    }

    @Override
    public int getDefaultEffectLevel() {
        return 1;
    }

    @Override
    public PotionEffectType getDefaultEffectType() {
        return PotionEffectType.LEVITATION;
    }

    @Override
    public Particle getDefaultParticle() {
        return Particle.PORTAL;
    }

    @Override
    public BlockData getParticleBlockData() {
        return null;
    }
}
