package master.gems;

import java.util.ArrayList;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import dev.iseal.powergems.misc.AbstractClasses.Gem;
import master.MPG;



public class EnderGem extends Gem {
    public EnderGem() {
        super("Ender");
    }

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    /** Teleports player to a random safe location nearby. */
    @Override
    protected void rightClick(Player player, int level) {
        int radius = level * 10;
        int attempts = 10;
        var rnd = java.util.concurrent.ThreadLocalRandom.current();
        var world = player.getWorld();


        try {
            for (int a = 0; a < attempts; a++) {
                var origin = player.getLocation();
                int dx = rnd.nextInt(-radius, radius + 1);
                int dz = rnd.nextInt(-radius, radius + 1);

                var unsafe = java.util.EnumSet.of(
                        Material.LAVA,
                        Material.FIRE,
                        Material.SOUL_FIRE,
                        Material.CACTUS,
                        Material.MAGMA_BLOCK,
                        Material.POWDER_SNOW
                );

                int x = origin.getBlockX() + dx;
                int z = origin.getBlockZ() + dz;

                var highest = world.getHighestBlockAt(x, z);
                var baseType = highest.getType();

                if (!baseType.isSolid()) continue;
                if (highest.isLiquid()) continue;
                if (unsafe.contains(baseType)) continue;


                var feet = highest.getLocation().add(0.5, 1, 0.5);
                var head = feet.clone().add(0, 1, 0);
                if (!feet.getBlock().isEmpty() || !head.getBlock().isEmpty()) continue;
                if (feet.getBlock().isLiquid() || head.getBlock().isLiquid()) continue;

                int y = feet.getBlockY();
                if (y < world.getMinHeight() || y > world.getMaxHeight()) continue;

                player.teleport(feet);
                player.sendMessage(Component.text("Teleported successfully!", NamedTextColor.DARK_PURPLE));
                break;
            }
        } catch (Exception e) {
            player.sendMessage(Component.text("Teleportation failed!", NamedTextColor.RED));
        }
    }

    /** Grants speed, strength, and haste buffs. */
    @Override
    protected void leftClick(Player player, int level) {
        int duration = 200 + 75 * level;
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.SPEED,
                duration,
                1,
                true,
                false
        ));
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.STRENGTH,
                duration,
                1,
                true,
                false
        ));
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.HASTE,
                duration,
                3,
                true,
                false
        ));
    }

    /** Spawns a damaging dragon breath cloud at your location. */
    @Override
    protected void shiftClick(Player player, int level)
    {
        int duration = 200 + level * 100;
        Location location = player.getLocation().add(0,0.1,0);
        AreaEffectCloud cloud = player.getWorld().spawn(location, AreaEffectCloud.class);
        cloud.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 100, 1), true);
        cloud.setRadius(1.0f + (level * 0.5f));
        cloud.setWaitTime(0);
        cloud.setReapplicationDelay(90);
        cloud.setSource(player);
        cloud.setParticle(Particle.DRAGON_BREATH, 1.0f);
        cloud.setDuration(duration);
        cloud.setRadiusPerTick(-0.02F);
    }


    @Override
    public ArrayList<String> getDefaultLore() {
        return new ArrayList<>() {{
            add("§5Level %level%");
            add("§5Abilities");
            add("§fRight click: Teleport to a random location nearby");
            add("§fLeft click: Gain Speed II, Strength II, and Haste IV");
            add("§fShift click: Summon dragon breath");
            if(MPG.PassiveLoreEnabled) {add("§bPassive: Night vision");}
        }};
    }

    @Override
    public PotionEffectType getDefaultEffectType() {
        return PotionEffectType.NIGHT_VISION;
    }

    @Override
    public int getDefaultEffectLevel() {
        return 1;
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
