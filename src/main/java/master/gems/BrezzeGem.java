package master.gems;

import dev.iseal.powergems.misc.AbstractClasses.Gem;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Breeze;
import org.bukkit.entity.Player;
import org.bukkit.entity.WindCharge;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class BrezzeGem extends Gem {
    public BrezzeGem() {
        super("Brezze");
    }

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    /** Launches multiple wind charges that knockback entities. */
    @Override
    protected void rightClick(Player player, int level) {
        for(int a  = 5 + level; a > 0; a--) {
            WindCharge windCharge = player.launchProjectile(WindCharge.class);
            windCharge.setGlowing(true);
            }
        }

    /** Dash forward in the direction you're facing. */
    @Override
    protected void leftClick(Player player, int level) {
        Location location = player.getLocation();
        player.setVelocity(location.getDirection().multiply(5));
        player.spawnParticle(Particle.CLOUD, location, 200, 1, 1, 1, 0.3);
    }

    /** Summons breeze mobs to fight for you. */
    @Override
    protected void shiftClick(Player player, int level) {
        Location spawnLocation = player.getLocation().add(0, 1, 0);
        int spawnCount = 2 + (level / 2); // Scale with level
        for (int a = 0; a < spawnCount; a++) {
            player.getWorld().spawn(spawnLocation, Breeze.class, breeze -> {
                breeze.setGlowing(true);
                var nearby = player.getNearbyEntities(20, 20, 20);
                for (var entity : nearby) {
                    if (entity instanceof org.bukkit.entity.Monster ||
                        (entity instanceof Player p && p != player)) {
                        breeze.setTarget((org.bukkit.entity.LivingEntity) entity);
                        break;
                    }
                }
            });
        }
    }

    @Override
    public ArrayList<String> getDefaultLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.AQUA + "Level %level%");
        lore.add(ChatColor.AQUA + "Abilities");
        lore.add(ChatColor.WHITE + "Left-Click: Dash Forward");
        lore.add(ChatColor.WHITE + "Right-Click: Launch Wind Charges");
        lore.add(ChatColor.WHITE + "Shift-Click: Summon Breezes");
        return lore;
    }

    @Override
    public PotionEffectType getDefaultEffectType() {
        return null;
    }

    @Override
    public int getDefaultEffectLevel() {
        return 0;
    }

    @Override
    public Particle getDefaultParticle() {
        return null;
    }

    @Override
    public BlockData getParticleBlockData() {
        return null;
    }
}
