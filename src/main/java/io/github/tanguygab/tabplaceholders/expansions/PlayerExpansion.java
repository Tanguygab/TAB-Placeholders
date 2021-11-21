package io.github.tanguygab.tabplaceholders.expansions;

import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.shared.TAB;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;

import me.neznamy.tab.api.placeholder.PlayerPlaceholder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PlayerExpansion extends Expansion {
	
	private Listener player_exp;
	private Listener player_level;
	private Listener player_gamemode;
	private Listener player_online;
	private Listener player_x;
	private Listener player_y;
	private Listener player_z;
	private Listener player_yaw;
	private Listener player_pitch;

	/**
	 * DONE:
	 * %player_level%
	 * %player_exp%
	 * %player_uuid%
	 * %player_ip%
	 * %player_first_join_date%
	 * %player_first_played%
	 * %player_first_join%
	 * %player_first_played_formatted%
	 * %player_gamemode%
	 * %player_online%
	 * %player_yaw%
	 * %player_pitch%
	 *
	 * IN TAB:
	 * Did anyways:
	 *   %player_name% -> %player%
	 *   %player_x% -> %xPos%
	 *   %player_y% -> %yPos%
	 *   %player_z% -> %zPos%
	 *
	 * %player_health% | %player_health_rounded%-> %health%
	 * %player_ping% -> %ping%
	 * %player_displayname% -> %displayname%
	 *
	 * NO EVENT:
	 * %player_is_op%
	 * %player_is_whitelisted%
	 * %player_is_banned%
	 *
	 * TODO:
	 * %player_allow_flight%
	 * %player_armor_helmet_name%
	 * %player_armor_helmet_data%
	 * %player_armor_chestplate_name%
	 * %player_armor_chestplate_data%
	 * %player_armor_leggings_name%
	 * %player_armor_leggings_data%
	 * %player_armor_boots_name%
	 * %player_armor_boots_data%
	 * %player_bed_x%
	 * %player_bed_y%
	 * %player_bed_z%
	 * %player_bed_world%
	 * %player_biome%
	 * %player_biome_capitalized%
	 * %player_can_pickup_items%
	 * %player_colored_ping%
	 * %player_compass_world%
	 * %player_compass_x%
	 * %player_compass_y%
	 * %player_compass_z%
	 * %player_custom_name%
	 * %player_current_exp%
	 * %player_direction%
	 * %player_direction_xz%
	 * %player_exp_to_level%
	 * %player_fly_speed%
	 * %player_food_level%
	 * %player_has_empty_slot%
	 * %player_has_played_before%
	 * %player_empty_slots%
	 * %player_has_potioneffect_<effect>%
	 * %player_has_permission_<permission>%
	 * %player_health_scale%
	 * %player_is_flying%
	 * %player_is_sneaking%
	 * %player_is_sprinting%
	 * %player_is_sleeping%
	 * %player_is_inside_vehicle%
	 * %player_item_in_hand%
	 * %player_item_in_hand_name%
	 * %player_item_in_hand_data%
	 * %player_item_in_hand_level_<enchantment>%
	 * %player_item_in_offhand%
	 * %player_item_in_offhand_name%
	 * %player_item_in_offhand_data%
	 * %player_item_in_offhand_level_<enchantment>%
	 * %player_locale%
	 * %player_locale_display_name%
	 * %player_locale_short%
	 * %player_locale_country%
	 * %player_locale_display_country%
	 * %player_last_damage%
	 * %player_last_played%
	 * %player_last_join%
	 * %player_last_played_formatted%
	 * %player_last_join_date%
	 * %player_light_level%
	 * %player_max_air%
	 * %player_max_health%
	 * %player_max_health_rounded%
	 * %player_max_no_damage_ticks%
	 * %player_minutes_lived%
	 * %player_no_damage_ticks%
	 * %player_ping_<playername>%
	 * %player_remaining_air%
	 * %player_saturation%
	 * %player_seconds_lived%
	 * %player_sleep_ticks%
	 * %player_thunder_duration%
	 * %player_ticks_lived%
	 * %player_time%
	 * %player_time_offset%
	 * %player_total_exp%
	 * %player_walk_speed%
	 * %player_weather_duration%
	 * %player_world%
	 * %player_world_type%
	 * %player_world_time_12%
	 * %player_world_time_24%
	 */
	
	public PlayerExpansion(Plugin plugin) {
		super(plugin);
	}
	
	@Override
	public void registerPlaceholders() {
		PlayerPlaceholder exp = manager.registerPlayerPlaceholder("%player_exp%", -1, p -> p(p).getExp()/ p(p).getExpToLevel());
		exp.enableTriggerMode(() -> {
			player_exp = new Listener() {
				
				@EventHandler(priority = EventPriority.MONITOR)
				public void onExpChange(PlayerExpChangeEvent e) {
					Player p = e.getPlayer();
					update(exp,p, p.getExp() + (float) e.getAmount() / p.getExpToLevel());
				}
			};
			register(player_exp);
		}, () -> unregister(player_exp));

		PlayerPlaceholder lvl = manager.registerPlayerPlaceholder("%player_level%", -1, p -> p(p).getLevel());
		lvl.enableTriggerMode(() -> {
			player_level = new Listener() {

				@EventHandler(priority = EventPriority.MONITOR)
				public void onLvlChange(PlayerLevelChangeEvent e) {
					update(lvl,e.getPlayer(), e.getNewLevel());
				}
			};
			register(player_level);
		}, () -> unregister(player_level));

		simpleRegisterPrefix("player");
		simpleRegister("ip", p->p(p).getAddress().getAddress().getHostAddress());
		simpleRegister("uuid", TabPlayer::getUniqueId);
		simpleRegister("name", TabPlayer::getName);
		simpleRegister("first_join", p->p(p).getFirstPlayed());
		simpleRegister("first_played", p->p(p).getFirstPlayed());
		SimpleDateFormat dateFormat = getDateFormat();
		simpleRegister("first_join_date", p-> dateFormat.format(new Date(p(p).getFirstPlayed())));
		simpleRegister("first_played_formatted", p-> dateFormat.format(new Date(p(p).getFirstPlayed())));

		PlayerPlaceholder gamemode = manager.registerPlayerPlaceholder("%player_gamemode%",-1,p->p(p).getGameMode().toString().toLowerCase());
		gamemode.enableTriggerMode(()->{
			player_gamemode = new Listener() {
				@EventHandler
				public void onGamemode(PlayerGameModeChangeEvent e) {
					update(gamemode,e.getPlayer());
				}
			};
			register(player_gamemode);
		},()->unregister(player_gamemode));

		PlayerPlaceholder isOnline = manager.registerPlayerPlaceholder("%player_is_op%",-1,p->p(p).isOnline());
		isOnline.enableTriggerMode(()->{
			player_online = new Listener() {
				@EventHandler
				public void onJoin(PlayerJoinEvent e) {
					update(isOnline,e.getPlayer(),true);
				}
				@EventHandler
				public void onLeave(PlayerQuitEvent e) {
					update(isOnline,e.getPlayer(),false);
				}
			};
			register(player_online);
		},()->unregister(player_online));

		registerPosPlaceholder("X");
		registerPosPlaceholder("Y");
		registerPosPlaceholder("Z");
		registerPosPlaceholder("Yaw");
		registerPosPlaceholder("Pitch");

	}

	private void registerPosPlaceholder(String name) {
		try {
			Method getLoc = Location.class.getMethod("get"+name);
			Field posListener = getClass().getDeclaredField("player_"+name.toLowerCase());

			PlayerPlaceholder pos = manager.registerPlayerPlaceholder("%player_"+name.toLowerCase()+"%",-1,p-> getLoc(getLoc,p(p).getLocation()));
			pos.enableTriggerMode(()->{
				try {
					posListener.set(this, new Listener() {
						@EventHandler
						public void onMove(PlayerMoveEvent e) {
							update(pos, e.getPlayer(), getLoc(getLoc,e.getTo()));
						}
					});
					register(getPosListener(posListener));
				} catch (Exception e) {e.printStackTrace();}
			},()->unregister(getPosListener(posListener)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Object getLoc(Method method, Location loc) {
		try {
			return method.invoke(loc);
		} catch (Exception e) {
			e.printStackTrace();
			return "NaN";
		}
	}

	private Listener getPosListener(Field field) {
		try {
			return (Listener) field.get(this);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	private SimpleDateFormat getDateFormat() {
		try {
			return new SimpleDateFormat(TAB.getInstance().getConfiguration().getConfig().getString("placeholders.date-format", "dd.MM.yyyy"), Locale.ENGLISH);
		} catch (IllegalArgumentException e) {return new SimpleDateFormat("dd.MM.yyyy");}
	}
}