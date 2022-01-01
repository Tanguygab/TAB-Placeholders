package io.github.tanguygab.tabplaceholders.expansions;

import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.placeholder.Placeholder;
import me.neznamy.tab.api.placeholder.PlaceholderManager;
import me.neznamy.tab.api.placeholder.PlayerPlaceholder;
import me.neznamy.tab.api.placeholder.ServerPlaceholder;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Expansion {

	protected Plugin plugin;
	private final PlaceholderManager manager;
	private final String prefix;

	public Expansion(Plugin plugin, String prefix) {
		this.plugin = plugin;
		this.prefix = prefix;
		manager = TabAPI.getInstance().getPlaceholderManager();
	}

	public abstract void registerPlaceholders();

	public Placeholder onPlaceholderRegister(String identifier) {
		return null;
	}

	public TabPlayer p(Player p) {
		return TabAPI.getInstance().getPlayer(p.getUniqueId());
	}
	public TabPlayer p(UUID p) {
		return TabAPI.getInstance().getPlayer(p);
	}
	public Player p(TabPlayer p) {
		return (Player) p.getPlayer();
	}

	public PlayerPlaceholder registerPlayer(String name, Function<TabPlayer, Object> run) {
		PlayerPlaceholder placeholder = manager.registerPlayerPlaceholder("%"+prefix+"_"+name+"%",-1,run);
		placeholder.enableTriggerMode();
		return placeholder;
	}
	public void update(PlayerPlaceholder pl, Player p) {
		TabPlayer player = p(p);
		pl.updateValue(player, pl.request(player));
	}
	public void update(PlayerPlaceholder pl, UUID p, Object value) {
		pl.updateValue(TabAPI.getInstance().getPlayer(p), value);
	}
	public void update(PlayerPlaceholder pl, Player p, Object value) {
		pl.updateValue(p(p), value);
	}


	public ServerPlaceholder registerServer(String name, Supplier<Object> run) {
		ServerPlaceholder placeholder = manager.registerServerPlaceholder("%"+prefix+"_"+name+"%",-1,run);
		placeholder.enableTriggerMode();
		return placeholder;
	}
	public void update(ServerPlaceholder pl) {
		pl.updateValue(pl.request());
	}


	public void register(Listener listener) {
		plugin.getServer().getPluginManager().registerEvents(listener,plugin);
	}
	public void unregister(Listener listener) {
		HandlerList.unregisterAll(listener);
	}

	public void delay(Runnable run) {
		plugin.getServer().getScheduler().runTaskLater(plugin,run,1);
	}

}