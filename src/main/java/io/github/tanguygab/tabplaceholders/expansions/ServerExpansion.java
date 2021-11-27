package io.github.tanguygab.tabplaceholders.expansions;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import me.neznamy.tab.api.placeholder.ServerPlaceholder;

public class ServerExpansion extends Expansion {

	private Listener server_online;
	private Listener server_unique_joins;
	private int uniqueJoinCount;
	
	public ServerExpansion(Plugin plugin) {
		super(plugin,"server");
	}
	
	/**
	 * DONE:
	 * %server_name%
	 * %server_online%
	 * %server_version%
	 * %server_max_players%
	 * %server_unique_joins%
	 * %server_ram_max%
	 * 
	 * TODO:
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
	 * %server_online_<world>%
	 * %server_has_whitelist%
	 * %server_total_chunks%
	 * %server_total_living_entities%
	 * %server_total_entities%
	 * %server_time_<SimpleDateFormat>%
	 * %server_countdown_<SimpleDateFormat>_<time>%
	 * e.g. %server_countdown_dd.MM.yyyy_01.01.2020%
	 */
	@Override
	public void registerPlaceholders() {
		registerServer("name", () -> plugin.getServer().getMotd());
		
		ServerPlaceholder online = registerServer("online", () -> plugin.getServer().getOnlinePlayers().size());
		online.enableTriggerMode(() -> {
			server_online = new Listener() {
				
				@EventHandler
				public void onJoin(PlayerJoinEvent e) {
					update(online);
				}
				
				@EventHandler
				public void onQuit(PlayerQuitEvent e) {
					update(online);
				}
			};
			register(server_online);
		}, () -> unregister(server_online));
		
		registerServer("version", () -> plugin.getServer().getBukkitVersion().split("-")[0]);
		registerServer("max_players", () -> plugin.getServer().getMaxPlayers());
		registerServer("ram_max", () -> Runtime.getRuntime().maxMemory()/1048576L);
		
		ServerPlaceholder uniqueJoins = registerServer("unique_joins",() -> plugin.getServer().getOfflinePlayers().length);
		uniqueJoins.enableTriggerMode(() -> {
			uniqueJoinCount = Integer.parseInt(uniqueJoins.request().toString());
			server_unique_joins = new Listener() {
				
				@EventHandler
				public void onJoin(PlayerJoinEvent e) {
					if (!e.getPlayer().hasPlayedBefore()) {
						uniqueJoinCount++;
						uniqueJoins.updateValue(uniqueJoinCount);
					}
				}
			};
			register(server_unique_joins);
		}, () -> {
			unregister(server_unique_joins);
		});


	}
}