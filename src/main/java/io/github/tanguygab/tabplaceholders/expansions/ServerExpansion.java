package io.github.tanguygab.tabplaceholders.expansions;

import me.neznamy.tab.api.placeholder.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import me.neznamy.tab.api.placeholder.ServerPlaceholder;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Collection of server placeholders not bound to any plugin
 *
 * Completed placeholders (7/24):
 * %server_name%
 * %server_online%
 * %server_version%
 * %server_max_players%
 * %server_unique_joins%
 * %server_ram_max%
 * %server_online_<world>%
 *
 * Missing placeholders:
 * %server_uptime%
 * %server_ram_used%
 * %server_ram_free%
 * %server_ram_total%
 * %server_tps%
 * %server_tps_1%
 * %server_tps_5%
 * %server_tps_15%
 * %server_tps_1_colored%
 * %server_tps_5_colored%
 * %server_tps_15_colored%
 * %server_has_whitelist%
 * %server_total_chunks%
 * %server_total_living_entities%
 * %server_total_entities%
 * %server_time_<SimpleDateFormat>%
 * %server_countdown_<SimpleDateFormat>_<time>%
 * e.g. %server_countdown_dd.MM.yyyy_01.01.2020%
 */
public class ServerExpansion extends Expansion {

	public ServerExpansion(Plugin plugin) {
		super(plugin,"server");
	}

	@Override
	public void registerPlaceholders() {
		registerServer("name", () -> plugin.getServer().getMotd());
		registerServer("version", () -> plugin.getServer().getBukkitVersion().split("-")[0]);
		registerServer("max_players", () -> plugin.getServer().getMaxPlayers());
		registerServer("ram_max", () -> Runtime.getRuntime().maxMemory()/1048576L);
		server_unique_joins();
		server_online();
	}

	private void server_online() {
		ServerPlaceholder online = registerServer("online", () -> plugin.getServer().getOnlinePlayers().size());
		Listener listener = new Listener() {

			@EventHandler
			public void onJoin(PlayerJoinEvent e) {
				update(online);
			}

			@EventHandler
			public void onQuit(PlayerQuitEvent e) {
				update(online);
			}
		};
		online.enableTriggerMode(() -> register(listener), () -> unregister(listener));
	}

	private void server_unique_joins() {
		AtomicInteger uniqueJoinCount = new AtomicInteger();
		ServerPlaceholder uniqueJoins = registerServer("unique_joins", () -> plugin.getServer().getOfflinePlayers().length);
		Listener listener = new Listener() {

			@EventHandler
			public void onJoin(PlayerJoinEvent e) {
				if (!e.getPlayer().hasPlayedBefore()) {
					uniqueJoins.updateValue(uniqueJoinCount.incrementAndGet());
				}
			}
		};
		uniqueJoins.enableTriggerMode(() -> {
			uniqueJoinCount.set(Integer.parseInt(uniqueJoins.request().toString()));
			register(listener);
		}, () -> unregister(listener));
	}

	private ServerPlaceholder server_online_WORLD(String identifier) {
		String world = identifier.substring(15, identifier.length()-1);
		ServerPlaceholder placeholder = registerServer("online_" + world, () ->
				Bukkit.getOnlinePlayers().stream().filter(player -> player.getWorld().getName().equals(world)).count());
		Listener listener = new Listener(){

			@EventHandler
			public void onJoin(PlayerJoinEvent e) {
				if (e.getPlayer().getWorld().getName().equals(world)) {
					placeholder.updateValue(placeholder.request());
				}
			}

			@EventHandler
			public void onQuit(PlayerQuitEvent e) {
				if (e.getPlayer().getWorld().getName().equals(world)) {
					placeholder.updateValue(placeholder.request());
				}
			}

			@EventHandler
			public void onWorldSwitch(PlayerChangedWorldEvent e) {
				if (e.getPlayer().getWorld().getName().equals(world) || e.getFrom().getName().equals(world)) {
					placeholder.updateValue(placeholder.request());
				}
			}
		};
		placeholder.enableTriggerMode(() -> register(listener), () -> unregister(listener));
		return placeholder;
	}

	@Override
	public Placeholder onPlaceholderRegister(String identifier) {
		if (identifier.startsWith("%server_online_")) {
			return server_online_WORLD(identifier);
		}
		return null;
	}
}