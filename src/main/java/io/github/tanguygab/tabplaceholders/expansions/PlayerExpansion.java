package io.github.tanguygab.tabplaceholders.expansions;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.plugin.Plugin;

import me.neznamy.tab.api.placeholder.PlaceholderManager;
import me.neznamy.tab.api.placeholder.PlayerPlaceholder;
import me.neznamy.tab.shared.TAB;

public class PlayerExpansion extends Expansion {
	
	private Listener player_exp;
	private Listener level;
	
	public PlayerExpansion(Plugin plugin) {
		super(plugin);
	}
	
	@Override
	public void registerPlaceholders() {
		PlaceholderManager manager = TAB.getInstance().getPlaceholderManager();
		
		PlayerPlaceholder exp = manager.registerPlayerPlaceholder("%player_exp%", -1, p -> ((Player)p.getPlayer()).getExp());
		exp.enableTriggerMode(() -> {
			player_exp = new Listener() {
				
				@EventHandler(priority = EventPriority.MONITOR)
				public void onExpChange(PlayerExpChangeEvent e) {
					update(exp,e.getPlayer(), e.getPlayer().getExp() + (float) e.getAmount() / e.getPlayer().getExpToLevel());
				}
			};
			register(player_exp);
		}, () -> unregister(player_exp));

		PlayerPlaceholder lvl = manager.registerPlayerPlaceholder("%player_level%", -1, p -> ((Player)p.getPlayer()).getLevel());
		lvl.enableTriggerMode(() -> {
			player_exp = new Listener() {

				@EventHandler(priority = EventPriority.MONITOR)
				public void onExpChange(PlayerLevelChangeEvent e) {
					update(lvl,e.getPlayer(), e.getNewLevel());
				}
			};
			register(player_exp);
		}, () -> unregister(player_exp));

	}
}