package io.github.tanguygab.tabplaceholders;

import io.github.tanguygab.tabplaceholders.expansions.*;
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

        new PlayerExpansion(this).registerPlaceholders();
        new ServerExpansion(this).registerPlaceholders();
        if (plm.isPluginEnabled("Marriage"))
            new MarriageExpansion(this).registerPlaceholders();
        if (plm.isPluginEnabled("Essentials"))
            new EssentialsExpansion(this).registerPlaceholders();
        if (plm.isPluginEnabled("NotQuests"))
            new NotQuestsExpansion(this).registerPlaceholders();

    }

}
