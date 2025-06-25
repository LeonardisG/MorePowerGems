package master.gems;

import master.utils.Utils;
import dev.iseal.powergems.misc.AbstractClasses.Gem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ShulkerGem has the following abilities:
 * <p>
 * Left-click: Launches a glowing Shulker Bullet projectile.
 * <p>
 * Right-click: Increases the player's armor toughness for 20 seconds times the level of the gem.
 * <p>
 * Shift-click: Applies levitation to nearby players in a radius, with duration and radius increasing with gem level.
 */
public class ShulkerGem extends Gem {
    /**
     * Constructs the ShulkerGem
     */
    public ShulkerGem() {
        super("Shulker");
    }
    /**
     * Processes the player's action
     *
     * @param act  the action performed
     * @param plr  the player using the gem
     * @param item the gem item
     */
    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    /**
     * Launches a Shulker Bullet projectile.
     * Each level increases the bullet speed by 20%.
     *
     * @param player the player who left-clicked
     * @param level  the level of the gem which affects the bullet speed
     */
    @Override
    protected void leftClick(Player player, int level) {
        ShulkerBullet bullet = player.launchProjectile(ShulkerBullet.class);
        bullet.setVelocity(bullet.getVelocity().multiply(1 + (level * 0.2)));
        bullet.setGlowing(true);
        bullet.setShooter(player);
    }

    /**
     * Increases the player's armor toughness.
     * Each level increases the toughness by 0.5.
     *
     * @param player the player who rights-clicked with the gem
     * @param level the level of the gem which affects the toughness amount
     */
    @Override
    protected void rightClick(Player player, int level) {
        AttributeInstance armor = player.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS);

        if (armor == null) return;

        UUID modifierUUID = UUID.nameUUIDFromBytes(("toughness_" + player.getUniqueId()).getBytes());

        // Remove existing modifier if present
        armor.getModifiers().stream()
                .filter(mod -> mod.getUniqueId().equals(modifierUUID))
                .forEach(armor::removeModifier);

        // Create and add new modifier
        AttributeModifier modifier = new AttributeModifier(
                modifierUUID,
                "Toughness Modifier",
                0.5 * level, // Increase toughness by 0.5 per level
                AttributeModifier.Operation.ADD_NUMBER
        );

        armor.addModifier(modifier);

        int duration = 20 + (level * 2); // 20 seconds + 2 seconds per level

        // Remove after 20 seconds
        Plugin plugin = Bukkit.getPluginManager().getPlugin("PowerGems");
        if (plugin != null) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                armor.removeModifier(modifier);
            }, duration);
        }

        player.sendMessage(ChatColor.AQUA + "Armor toughness increased by " + (0.5 * level) + " for 20 seconds!");

        Utils.startTimer(plugin, player, duration, "Your armor toughness buff has expired!");
    }

    /**
     * Applies levitation to players around the gem user.
     * Each level increases the radius by 3 blocks.
     * Also for each level, the effect duration is increased by 100 ticks (5 seconds).
     *
     * @param player the player who shifts-clicked with the gem
     * @param level  the level of the gem which affects the levitation duration
     */
    @Override
    protected void shiftClick(Player player, int level) {
        int radius = 15 + (3 * level);
        List<Entity> nearbyEntities = player.getNearbyEntities(radius, radius, radius);

        for(Entity entity : nearbyEntities) {
            if(entity instanceof Player targetPlayer && targetPlayer != player) {
                targetPlayer.addPotionEffect(new PotionEffect(
                        PotionEffectType.LEVITATION,
                        100 * level,
                        1
                ));
            }
        }

        player.sendMessage(ChatColor.LIGHT_PURPLE + "Applied levitation to nearby players!");
    }
    /**
     * Returns the default lore for the gem.
     *
     * @return A list of strings representing the gem's lore
     */
    @Override
    public ArrayList<String> getDefaultLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Level %level%");
        lore.add(ChatColor.GREEN + "Abilities");
        lore.add(ChatColor.WHITE
                + "Right click: Increase armor toughness for 20+ seconds");
        lore.add(ChatColor.WHITE
                + "Shift click: Levitate nearby players in a radius");
        lore.add(ChatColor.WHITE
                + "Left click: Launch a glowing Shulker Bullet projectile");
        return lore;
    }

    /**
     * Returns the default effect level for the gem.
     *
     * @return The default effect level
     */
    @Override
    public int getDefaultEffectLevel() {
        return 1;
    }

    /**
     * Returns the default potion effect type for this gem
     *
     * @return the default potion effect type
     */
    @Override
    public PotionEffectType getDefaultEffectType() {
        return PotionEffectType.JUMP;
    }
}
