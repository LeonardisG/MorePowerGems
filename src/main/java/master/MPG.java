package master;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.api.ApiManager;
import master.gems.*;
import master.listeners.DoubleDropsListener;
import master.listeners.TradeListener;
import master.listeners.WitherDamageListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class MPG extends JavaPlugin {

    @Override
    public void onEnable() {
        Logger logger = PowerGems.getPlugin().getLogger();
        ApiManager api = ApiManager.getInstance();

        api.registerGemClass(AffluenceGem.class, this);
        api.registerGemClass(PoisonGem.class, this);
        api.registerGemClass(RuinGem.class, this);
        api.registerGemClass(ShulkerGem.class, this);
        api.registerGemClass(WitherGem.class, this);
        logger.info("Registered MorePowerGems gems");

        // Listeners
        getServer().getPluginManager().registerEvents(new TradeListener(), this);
        getServer().getPluginManager().registerEvents(new DoubleDropsListener(), this);
        getServer().getPluginManager().registerEvents(new WitherDamageListener(), this);
        logger.info("Registered MorePowerGems listeners");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
