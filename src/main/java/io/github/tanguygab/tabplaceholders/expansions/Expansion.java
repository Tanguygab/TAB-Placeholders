package io.github.tanguygab.tabplaceholders.expansions;

import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.placeholder.PlaceholderManager;
import me.neznamy.tab.api.placeholder.PlayerPlaceholder;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.UUID;
import java.util.function.Function;

public abstract class Expansion {

	protected Plugin plugin;
	protected PlaceholderManager manager;
	private final String prefix;

	public Expansion(Plugin plugin) {
		this(plugin,"");
	}
	public Expansion(Plugin plugin, String prefix) {
		this.plugin = plugin;
		this.prefix = prefix;
		manager = TabAPI.getInstance().getPlaceholderManager();
	}

	public abstract void registerPlaceholders();

	public TabPlayer p(Player p) {
		return TabAPI.getInstance().getPlayer(p.getUniqueId());
	}
	public TabPlayer p(UUID p) {
		return TabAPI.getInstance().getPlayer(p);
	}

	public Player p(TabPlayer p) {
		return (Player) p.getPlayer();
	}

	public void update(PlayerPlaceholder pl, Player p) {
		TabPlayer player = p(p);
		pl.updateValue(player, pl.request(player));
	}
	public void update(PlayerPlaceholder pl, UUID p) {
		TabPlayer player = TabAPI.getInstance().getPlayer(p);
		pl.updateValue(player, pl.request(player));
	}

	public void update(PlayerPlaceholder pl, UUID p, Object value) {
		pl.updateValue(TabAPI.getInstance().getPlayer(p), value);
	}
	public void update(PlayerPlaceholder pl, Player p, Object value) {
		pl.updateValue(p(p), value);
	}

	public PlayerPlaceholder simpleRegister(String name, Function<TabPlayer, Object> run) {
		PlayerPlaceholder placeholder = manager.registerPlayerPlaceholder("%"+prefix+"_"+name+"%",-1,run);
		placeholder.enableTriggerMode();
		return placeholder;
	}

	public void register(Listener listener) {
		plugin.getServer().getPluginManager().registerEvents(listener,plugin);
	}

	public void unregister(Listener listener) {
		HandlerList.unregisterAll(listener);
	}

}