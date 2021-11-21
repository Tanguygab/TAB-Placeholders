package io.github.tanguygab.tabplaceholders.expansions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.earth2me.essentials.Essentials;

import me.neznamy.tab.api.placeholder.PlaceholderManager;
import me.neznamy.tab.api.placeholder.PlayerPlaceholder;
import me.neznamy.tab.shared.TAB;
import net.ess3.api.events.AfkStatusChangeEvent;
import net.ess3.api.events.GodStatusChangeEvent;
import net.ess3.api.events.NickChangeEvent;

public class EssentialsExpansion extends Expansion {

    private Listener essentials_afk;
    private Listener essentials_afk_reason;
    private Listener essentials_godmode;
    private Listener essentials_nickname;
    private Listener essentials_nickname_stripped;

    public EssentialsExpansion(Plugin plugin) {
        super(plugin);
    }

    /**
     * DONE:
     * %essentials_afk%
     * %essentials_afk_reason%
     * %essentials_godmode%
     * %essentials_nickname%
     * %essentials_nickname_stripped%
     *
     * TODO:
     * %essentials_fly%
     * %essentials_has_kit_<kitname>%
     * %essentials_home_<number>
     * %essentials_home_<number>_<x|y|z>%
     * %essentials_homes_set%
     * %essentials_homes_max%
     * %essentials_is_muted%
     * %essentials_is_pay_confirm%
     * %essentials_is_pay_enabled%
     * %essentials_is_teleport_enabled%
     * %essentials_jailed%
     * %essentials_jailed_time_remaining%
     * %essentials_kit_is_available_<kitname>%
     * %essentials_kit_last_use_<kitname>%
     * %essentials_kit_time_until_available_<kitname>%
     * %essentials_kit_time_until_available_raw_<kitname>%
     * %essentials_msg_ignore%
     * %essentials_pm_recipient%
     * %essentials_safe_online%
     * %essentials_unique%
     * %essentials_vanished%
     * %essentials_world_date%
     * %essentials_world_time%
     * %essentials_world_time_24%
     * %essentials_worth%
     * %essentials_worth:<item>%
     */
    @Override
    public void registerPlaceholders() {
        PlaceholderManager manager = TAB.getInstance().getPlaceholderManager();
        Essentials essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");

        PlayerPlaceholder afk = manager.registerPlayerPlaceholder("%essentials_afk%", -1, p -> essentials.getUser(p.getUniqueId()).isAfk());
        afk.enableTriggerMode(() -> {
            essentials_afk = new Listener() {

                @EventHandler(priority = EventPriority.MONITOR)
                public void onAfkChange(AfkStatusChangeEvent e) {
                    afk.updateValue(TAB.getInstance().getPlayer(e.getAffected().getName()), e.getValue());
                }
            };
            register(essentials_afk);
        }, () -> unregister(essentials_afk));

        PlayerPlaceholder afk_reason = manager.registerPlayerPlaceholder("%essentials_afk_reason%", -1, p -> {
            String msg = essentials.getUser(p.getUniqueId()).getAfkMessage();
            return msg == null ? "" : msg;
        });
        afk_reason.enableTriggerMode(() -> {
            essentials_afk_reason = new Listener() {

                @EventHandler(priority = EventPriority.MONITOR)
                public void onAfkChange(AfkStatusChangeEvent e) {
                    //message is not updated in event and getter is missing
                    Bukkit.getScheduler().runTaskLater(plugin, () -> afk_reason.updateValue(TAB.getInstance().getPlayer(e.getAffected().getName()), e.getAffected().getAfkMessage() == null ? "" : e.getAffected().getAfkMessage()), 1);
                }
            };
            register(essentials_afk_reason);
        }, () -> unregister(essentials_afk_reason));

        PlayerPlaceholder god = manager.registerPlayerPlaceholder("%essentials_godmode%", -1, p -> essentials.getUser(p.getUniqueId()).isGodModeEnabled());
        god.enableTriggerMode(() -> {
            essentials_godmode = new Listener() {

                @EventHandler(priority = EventPriority.MONITOR)
                public void onGodChange(GodStatusChangeEvent e) {
                    god.updateValue(TAB.getInstance().getPlayer(e.getAffected().getName()), e.getValue());
                }
            };
            register(essentials_godmode);
        }, () -> unregister(essentials_godmode));

        PlayerPlaceholder nick = manager.registerPlayerPlaceholder("%essentials_nickname%", -1, p -> essentials.getUser(p.getUniqueId()).getNickname() == null ? p.getName() : essentials.getUser(p.getUniqueId()).getNickname());
        nick.enableTriggerMode(() -> {
            essentials_nickname = new Listener() {

                @EventHandler(priority = EventPriority.MONITOR)
                public void onNickChange(NickChangeEvent e) {
                    nick.updateValue(TAB.getInstance().getPlayer(e.getAffected().getName()), e.getValue() == null ? e.getAffected().getName() : e.getValue());
                }
            };
            register(essentials_nickname);
        }, () -> unregister(essentials_nickname));

        PlayerPlaceholder nickStripped = manager.registerPlayerPlaceholder("%essentials_nickname_stripped%", -1, p -> ChatColor.stripColor(essentials.getUser(p.getUniqueId()).getNickname() == null ? p.getName() : essentials.getUser(p.getUniqueId()).getNickname()));
        nickStripped.enableTriggerMode(() -> {
            essentials_nickname_stripped = new Listener() {

                @EventHandler(priority = EventPriority.MONITOR)
                public void onNickChange(NickChangeEvent e) {
                    nickStripped.updateValue(TAB.getInstance().getPlayer(e.getAffected().getName()), ChatColor.stripColor(e.getValue() == null ? e.getAffected().getName() : e.getValue()));
                }
            };
            register(essentials_nickname_stripped);
        }, () -> unregister(essentials_nickname_stripped));
    }
}