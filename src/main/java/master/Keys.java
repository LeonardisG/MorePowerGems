package master;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

/** Central NamespacedKeys (initialized once in onEnable). */
public final class Keys {
    private Keys() {}

    public static NamespacedKey AMETHYST_PROJECTILE;
    public static NamespacedKey AMETHYST_LEVEL;
    public static NamespacedKey EVOKER_OWNER;

    public static NamespacedKey WITHER_SKULL;
    public static NamespacedKey WITHER_SKULL_LEVEL;
    public static NamespacedKey WITHER_DAMAGE_REDUCTION;

    public static NamespacedKey MAGIC_FLY;
    public static NamespacedKey AMETHYST_TRAP;
    public static NamespacedKey AFFLUENCE_DOUBLE_DROPS;
    public static NamespacedKey AFFLUENCE_DISCOUNT;

    public static void init(JavaPlugin plugin) {
        AMETHYST_PROJECTILE = new NamespacedKey(plugin, "amethyst_projectile");
        AMETHYST_LEVEL = new NamespacedKey(plugin, "amethyst_level");
        EVOKER_OWNER = new NamespacedKey(plugin, "evoker_owner");

        WITHER_SKULL = new NamespacedKey(plugin, "wither_skull");
        WITHER_SKULL_LEVEL = new NamespacedKey(plugin, "wither_skull_level");
        WITHER_DAMAGE_REDUCTION = new NamespacedKey(plugin, "wither_damage_reduction");

        MAGIC_FLY = new NamespacedKey(plugin, "magic_fly");
        AMETHYST_TRAP = new NamespacedKey(plugin, "amethyst_trap");
        AFFLUENCE_DOUBLE_DROPS = new NamespacedKey(plugin, "affluence_double_drops");
        AFFLUENCE_DISCOUNT = new NamespacedKey(plugin, "affluence_discount");
    }
}

