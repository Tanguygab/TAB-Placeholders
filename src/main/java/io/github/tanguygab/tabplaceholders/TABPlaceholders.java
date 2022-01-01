package io.github.tanguygab.tabplaceholders;

import io.github.tanguygab.tabplaceholders.expansions.*;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.event.plugin.PlaceholderRegisterEvent;
import me.neznamy.tab.api.event.plugin.TabLoadEvent;
import me.neznamy.tab.api.placeholder.Placeholder;
import me.neznamy.tab.shared.TAB;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class TABPlaceholders extends JavaPlugin {

    private final Map<Function<Plugin, Expansion>, String> allExpansions = new HashMap<>(){{
        put(PlayerExpansion::new, null);
        put(ServerExpansion::new, null);
        put(MarriageExpansion::new, "Marriage");
        put(EssentialsExpansion::new, "Essentials");
        put(NotQuestsExpansion::new, "NotQuests");
    }};

    private final List<Expansion> activeExpansions = new ArrayList<>();

    @Override
    public void onEnable() {
        reload();
        TabAPI.getInstance().getEventBus().register(TabLoadEvent.class, event -> reload());
        TabAPI.getInstance().getEventBus().register(PlaceholderRegisterEvent.class, event -> {
            for (Expansion e : activeExpansions) {
                Placeholder p = e.onPlaceholderRegister(event.getIdentifier());
                if (p != null) event.setPlaceholder(p);
            }
        });
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    public void reload() {
        activeExpansions.clear();
        for (Map.Entry<Function<Plugin, Expansion>, String> entry : allExpansions.entrySet()) {
            if (entry.getValue() == null || Bukkit.getPluginManager().isPluginEnabled(entry.getValue())) {
                Expansion e = entry.getKey().apply(this);
                activeExpansions.add(e);
                e.registerPlaceholders();
            }
        }
        for (Expansion e : activeExpansions) {
            for (String identifier : TabAPI.getInstance().getPlaceholderManager().getUsedPlaceholders()) {
                Placeholder p = e.onPlaceholderRegister(identifier);
                if (p != null) TAB.getInstance().getPlaceholderManager().registerPlaceholder(p);
            }
        }
    }
}