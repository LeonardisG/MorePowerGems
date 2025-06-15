package master;

import master.gems.*;

import master.listeners.DoubleDropsListener;
import master.listeners.TradeListener;
import dev.iseal.powergems.api.ApiManager;
import master.listeners.WitherDamageListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class MPG extends JavaPlugin {

    ApiManager apiManager;
    Logger logger = Logger.getLogger("PowerGems");

    @Override
    public void onEnable() {
        apiManager = new ApiManager();

        apiManager.registerAddonPlugin(this);
        logger.info("Registered MorePowerGems as an addon plugin for PowerGems.");

        apiManager.registerGemClass(AffluenceGem.class);
        apiManager.registerGemClass(PoisonGem.class);
        apiManager.registerGemClass(RuinGem.class);
        apiManager.registerGemClass(ShulkerGem.class);
        apiManager.registerGemClass(WitherGem.class);
        logger.info("Registered MorePowerGems gems");

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
