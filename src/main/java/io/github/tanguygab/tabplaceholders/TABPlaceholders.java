package io.github.tanguygab.tabplaceholders;

import io.github.tanguygab.tabplaceholders.expansions.MarriageExpansion;
import io.github.tanguygab.tabplaceholders.expansions.PlayerExpansion;
import io.github.tanguygab.tabplaceholders.expansions.ServerExpansion;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.placeholder.PlaceholderManager;
import me.neznamy.tab.platforms.bukkit.event.TabLoadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class TABPlaceholders extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this,this);
        reload();
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll((Plugin) this);
    }

    @EventHandler
    public void onReload(TabLoadEvent e) {
        reload();
    }

    public void reload() {
        PluginManager plm = getServer().getPluginManager();
        PlaceholderManager pm = TabAPI.getInstance().getPlaceholderManager();

        new PlayerExpansion(this).registerPlaceholders();
        new ServerExpansion(this).registerPlaceholders();
        if (plm.isPluginEnabled("Marriage"))
            new MarriageExpansion(this).registerPlaceholders();

    }

}
