package io.github.tanguygab.tabplaceholders.expansions;

import com.earth2me.essentials.User;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.placeholder.ServerPlaceholder;
import net.ess3.api.IUser;
import net.ess3.api.events.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import com.earth2me.essentials.Essentials;

import me.neznamy.tab.api.placeholder.PlayerPlaceholder;

import java.util.stream.StreamSupport;

/**
 * Expansion for plugin Essentials
 * Link: https://essentialsx.net/
 *
 * Completed placeholders (10/31):
 * %essentials_afk%
 * %essentials_afk_reason%
 * %essentials_godmode%
 * %essentials_nickname%
 * %essentials_nickname_stripped%
 * %essentials_fly%
 * %essentials_safe_online%
 * %essentials_jailed%
 * %essentials_is_muted%
 * %essentials_vanished%
 *
 * Missing placeholders:
 * %essentials_has_kit_<kitname>%
 * %essentials_home_<number>
 * %essentials_home_<number>_<x|y|z>%
 * %essentials_homes_set%
 * %essentials_homes_max%
 * %essentials_is_pay_confirm%
 * %essentials_is_pay_enabled%
 * %essentials_is_teleport_enabled%
 * %essentials_jailed_time_remaining%
 * %essentials_kit_is_available_<kitname>%
 * %essentials_kit_last_use_<kitname>%
 * %essentials_kit_time_until_available_<kitname>%
 * %essentials_kit_time_until_available_raw_<kitname>%
 * %essentials_msg_ignore%
 * %essentials_pm_recipient%
 * %essentials_unique%
 * %essentials_world_date%
 * %essentials_world_time%
 * %essentials_world_time_24%
 * %essentials_worth%
 * %essentials_worth:<item>%
 */
public class EssentialsExpansion extends Expansion {

    private final Essentials ess;

    public EssentialsExpansion(Plugin plugin) {
        super(plugin,"essentials");
        ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
    }

    @Override
    public void registerPlaceholders() {
        essentials_afk();
        essentials_afk_reason();
        essentials_godmode();
        essentials_nickname();
        essentials_nickname_stripped();
        essentials_fly();
        essentials_safe_online();
        essentials_jailed();
        essentials_is_muted();
        essentials_vanished();
    }

    private void essentials_afk() {
        PlayerPlaceholder afk = registerPlayer("afk", p -> user(p).isAfk());
        Listener listener = new Listener() {

            @EventHandler(priority = EventPriority.MONITOR)
            public void onAfkChange(AfkStatusChangeEvent e) {
                update(afk, e.getAffected().getBase(), e.getValue());
            }
        };
        afk.enableTriggerMode(() -> register(listener), () -> unregister(listener));
    }

    private void essentials_afk_reason() {
        PlayerPlaceholder afk_reason = registerPlayer("afk_reason", p -> user(p).getAfkMessage() == null ? "" : user(p).getAfkMessage());
        Listener listener = new Listener() {

            @EventHandler(priority = EventPriority.MONITOR)
            public void onAfkChange(AfkStatusChangeEvent e) {
                delay(() -> update(afk_reason, e.getAffected().getBase(), e.getAffected().getAfkMessage() == null ? "" : e.getAffected().getAfkMessage()));
            }
        };
        afk_reason.enableTriggerMode(() -> register(listener), () -> unregister(listener));
    }

    private void essentials_godmode() {
        PlayerPlaceholder god = registerPlayer("godmode", p -> user(p).isGodModeEnabled());
        Listener listener = new Listener() {

            @EventHandler(priority = EventPriority.MONITOR)
            public void onGodChange(GodStatusChangeEvent e) {
                update(god, e.getAffected().getBase(), e.getValue());
            }
        };
        god.enableTriggerMode(() -> register(listener), () -> unregister(listener));
    }

    private void essentials_nickname() {
        PlayerPlaceholder nick = registerPlayer("nickname", p -> user(p).getNickname() == null ? p.getName() : user(p).getNickname());
        Listener listener = new Listener() {

            @EventHandler(priority = EventPriority.MONITOR)
            public void onNickChange(NickChangeEvent e) {
                IUser user = e.getAffected();
                update(nick, user.getBase(), e.getValue() == null ? user.getName() : e.getValue());
            }
        };
        nick.enableTriggerMode(() -> register(listener), () -> unregister(listener));
    }

    private void essentials_nickname_stripped() {
        PlayerPlaceholder nickStripped = registerPlayer("nickname_stripped", p -> ChatColor.stripColor(ess.getUser(p.getUniqueId()).getNickname() == null ? p.getName() : ess.getUser(p.getUniqueId()).getNickname()));
        Listener listener = new Listener() {

            @EventHandler(priority = EventPriority.MONITOR)
            public void onNickChange(NickChangeEvent e) {
                IUser user = e.getAffected();
                update(nickStripped, user.getBase(), ChatColor.stripColor(e.getValue() == null ? user.getName() : e.getValue()));
            }
        };
        nickStripped.enableTriggerMode(() -> register(listener), () -> unregister(listener));
    }

    private void essentials_fly() {
        PlayerPlaceholder fly = registerPlayer("fly", p -> user(p).getBase().getAllowFlight());
        Listener listener = new Listener() {

            @EventHandler(priority = EventPriority.MONITOR)
            public void onFly(FlyStatusChangeEvent e) {
                update(fly, e.getAffected().getBase(), e.getValue());
            }
        };
        fly.enableTriggerMode(() -> register(listener), () -> unregister(listener));
    }

    private void essentials_safe_online() {
        ServerPlaceholder safeOnline = registerServer("safe_online", () ->
                StreamSupport.stream(ess.getOnlineUsers().spliterator(), false).filter(user1 -> !user1.isHidden()).count());
        Listener listener = new Listener() {

            @EventHandler
            public void onJoin(PlayerJoinEvent e) {
                delay(()->update(safeOnline));
            }

            @EventHandler
            public void onVanish(VanishStatusChangeEvent e) {
                delay(()->update(safeOnline));
            }

            @EventHandler
            public void onQuit(PlayerQuitEvent e) {
                delay(()->update(safeOnline));
            }
        };
        safeOnline.enableTriggerMode(() -> register(listener), () -> unregister(listener));
    }

    private void essentials_jailed() {
        PlayerPlaceholder jailed = registerPlayer("jailed", p -> user(p).isJailed());
        Listener listener = new Listener() {

            @EventHandler(priority = EventPriority.MONITOR)
            public void onJail(JailStatusChangeEvent e) {
                update(jailed, e.getAffected().getBase(), e.getValue());
            }
        };
        jailed.enableTriggerMode(() -> register(listener), () -> unregister(listener));
    }

    private void essentials_is_muted() {
        PlayerPlaceholder muted = registerPlayer("is_muted", p -> user(p).isMuted());
        Listener listener = new Listener() {

            @EventHandler(priority = EventPriority.MONITOR)
            public void onMute(MuteStatusChangeEvent e) {
                update(muted, e.getAffected().getBase(), e.getValue());
            }
        };
        muted.enableTriggerMode(() -> register(listener), () -> unregister(listener));
    }

    private void essentials_vanished() {
        PlayerPlaceholder vanished = registerPlayer("vanished", p -> user(p).isVanished());
        Listener listener = new Listener() {

            @EventHandler(priority = EventPriority.MONITOR)
            public void onVanish(VanishStatusChangeEvent e) {
                update(vanished, e.getAffected().getBase(), e.getValue());
            }
        };
        vanished.enableTriggerMode(() -> register(listener), () -> unregister(listener));
    }

    private User user(TabPlayer p) {
        return ess.getUser(p.getUniqueId());
    }
}
