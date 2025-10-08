package master;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

/** Central NamespacedKeys (initialized once in onEnable). */
public final class Keys {
    private Keys() {}

    public static NamespacedKey AMETHYST_PROJECTILE;
    public static NamespacedKey AMETHYST_LEVEL;
    public static NamespacedKey EVOKER_OWNER;

    public static void init(JavaPlugin plugin) {
        AMETHYST_PROJECTILE = new NamespacedKey(plugin, "amethyst_projectile");
        AMETHYST_LEVEL = new NamespacedKey(plugin, "amethyst_level");
        EVOKER_OWNER = new NamespacedKey(plugin, "evoker_owner");

    }
}

