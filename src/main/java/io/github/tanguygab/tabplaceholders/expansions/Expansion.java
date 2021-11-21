package io.github.tanguygab.tabplaceholders.expansions;

import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.placeholder.PlayerPlaceholder;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public abstract class Expansion {

	protected Plugin plugin;

	public Expansion(Plugin plugin) {
		this.plugin = plugin;
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


	public void register(Listener listener) {
		plugin.getServer().getPluginManager().registerEvents(listener,plugin);
	}

	public void unregister(Listener listener) {
		HandlerList.unregisterAll(listener);
	}

}