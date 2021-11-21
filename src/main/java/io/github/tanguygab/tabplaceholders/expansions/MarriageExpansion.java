package io.github.tanguygab.tabplaceholders.expansions;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.lenis0012.bukkit.marriage2.MPlayer;
import com.lenis0012.bukkit.marriage2.events.PlayerDivorceEvent;
import com.lenis0012.bukkit.marriage2.events.PlayerMarryEvent;
import com.lenis0012.bukkit.marriage2.internal.MarriagePlugin;

import me.neznamy.tab.api.placeholder.PlayerPlaceholder;

public class MarriageExpansion extends Expansion {
	
	private Listener marriage_is_married;
	private Listener marriage_partner;
	
	
	public MarriageExpansion(Plugin plugin) {
		super(plugin);
	}

	/**
	 * DONE:
	 * %marriage_is_married%
	 * %marriage_partner%
	 * 
	 * TODO: (actually, there isn't any other event...)
	 * %marriage_is_priest%
	 * %marriage_gender%
	 * %marriage_gender_chat_prefix%
	 * %marriage_last_name%
	 * %marriage_has_pvp_enabled%
	 * %marriage_has_home_set%
	 * %marriage_home_x%
	 * %marriage_home_y%
	 * %marriage_home_z%
	 */
	@Override
	public void registerPlaceholders() {
		PlayerPlaceholder isMarried = manager.registerPlayerPlaceholder("%marriage_is_married%", -1, p -> MarriagePlugin.getCore().getMPlayer(p.getUniqueId()).isMarried());
		isMarried.enableTriggerMode(() -> {
			marriage_is_married = new Listener() {
				
				@EventHandler
				public void a(PlayerMarryEvent e) {
					update(isMarried,e.getRequesing().getUniqueId(),true);
					update(isMarried,e.getRequested().getUniqueId(),true);
				}
				
				@EventHandler
				public void a(PlayerDivorceEvent e) {
					update(isMarried,e.getMarriage().getPlayer1Id(),false);
					update(isMarried,e.getMarriage().getPllayer2Id(),false); //nice typo btw // agreed lol
				}
			};
			register(marriage_is_married);
		}, () -> unregister(marriage_is_married));
		
		PlayerPlaceholder partner = manager.registerPlayerPlaceholder("%marriage_partner%", -1, p -> {
			MPlayer player = MarriagePlugin.getCore().getMPlayer(p.getUniqueId());
			return player.isMarried() ? p(player.getMarriage().getOtherPlayer(player.getUniqueId())).getName() : "";
		});

		partner.enableTriggerMode(() -> {
			marriage_partner = new Listener() {
				
				@EventHandler
				public void a(PlayerMarryEvent e) {
					MPlayer requesting = e.getRequesing(); //requesTing :P
					MPlayer requested = e.getRequested();
					update(partner,requesting.getUniqueId(),requested.getLastName());
					update(partner,requested.getUniqueId(),requesting.getLastName());
				}
				
				@EventHandler
				public void a(PlayerDivorceEvent e) {
					update(partner,e.getMarriage().getPlayer1Id(),"");
					update(partner,e.getMarriage().getPllayer2Id(),"");
				}
			};
			register(marriage_partner);
		}, () -> unregister(marriage_partner));
	}
}