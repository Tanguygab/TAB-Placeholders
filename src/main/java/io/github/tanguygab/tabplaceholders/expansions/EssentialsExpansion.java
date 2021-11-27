package io.github.tanguygab.tabplaceholders.expansions;

import com.earth2me.essentials.User;
import me.neznamy.tab.api.TabPlayer;
import net.ess3.api.IUser;
import net.ess3.api.events.FlyStatusChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.earth2me.essentials.Essentials;

import me.neznamy.tab.api.placeholder.PlayerPlaceholder;
import net.ess3.api.events.AfkStatusChangeEvent;
import net.ess3.api.events.GodStatusChangeEvent;
import net.ess3.api.events.NickChangeEvent;

public class EssentialsExpansion extends Expansion {

    private Listener essentials_afk;
    private Listener essentials_afk_reason;
    private Listener essentials_godmode;
    private Listener essentials_nickname;
    private Listener essentials_nickname_stripped;
    private Listener essentials_fly;

    private final Essentials ess;

    public EssentialsExpansion(Plugin plugin) {
        super(plugin,"essentials");
        ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
    }

    /**
     * Plugin URL: https://essentialsx.net/
     *
     * DONE:
     * %essentials_afk%
     * %essentials_afk_reason%
     * %essentials_godmode%
     * %essentials_nickname%
     * %essentials_nickname_stripped%
     * %essentials_fly%
     *
     * TODO:
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
        PlayerPlaceholder afk = registerPlayer("afk", p -> user(p).isAfk());
        afk.enableTriggerMode(() -> {
            essentials_afk = new Listener() {

                @EventHandler(priority = EventPriority.MONITOR)
                public void onAfkChange(AfkStatusChangeEvent e) {
                    afk.updateValue(player(e.getAffected()), e.getValue());
                }
            };
            register(essentials_afk);
        }, () -> unregister(essentials_afk));

        PlayerPlaceholder afk_reason = registerPlayer("afk_reason", p -> {
            String msg = user(p).getAfkMessage();
            return msg == null ? "" : msg;
        });
        afk_reason.enableTriggerMode(() -> {
            essentials_afk_reason = new Listener() {

                @EventHandler(priority = EventPriority.MONITOR)
                public void onAfkChange(AfkStatusChangeEvent e) {
                    //message is not updated in event and getter is missing
                    IUser user = e.getAffected();
                    Bukkit.getScheduler().runTaskLater(plugin, () -> update(afk_reason,user.getBase(), user.getAfkMessage() == null ? "" : user.getAfkMessage()), 1);
                }
            };
            register(essentials_afk_reason);
        }, () -> unregister(essentials_afk_reason));

        PlayerPlaceholder god = registerPlayer("godmode", p -> user(p).isGodModeEnabled());
        god.enableTriggerMode(() -> {
            essentials_godmode = new Listener() {

                @EventHandler(priority = EventPriority.MONITOR)
                public void onGodChange(GodStatusChangeEvent e) {
                    update(god,e.getAffected().getBase(),e.getValue());
                }
            };
            register(essentials_godmode);
        }, () -> unregister(essentials_godmode));

        PlayerPlaceholder nick = registerPlayer("nickname", p -> user(p).getNickname() == null ? p.getName() : user(p).getNickname());
        nick.enableTriggerMode(() -> {
            essentials_nickname = new Listener() {

                @EventHandler(priority = EventPriority.MONITOR)
                public void onNickChange(NickChangeEvent e) {
                    IUser user = e.getAffected();
                    update(nick,user.getBase(),e.getValue() == null ? user.getName() : e.getValue());
                }
            };
            register(essentials_nickname);
        }, () -> unregister(essentials_nickname));

        PlayerPlaceholder nickStripped = registerPlayer("nickname_stripped", p -> ChatColor.stripColor(ess.getUser(p.getUniqueId()).getNickname() == null ? p.getName() : ess.getUser(p.getUniqueId()).getNickname()));
        nickStripped.enableTriggerMode(() -> {
            essentials_nickname_stripped = new Listener() {

                @EventHandler(priority = EventPriority.MONITOR)
                public void onNickChange(NickChangeEvent e) {
                    IUser user = e.getAffected();
                    update(nickStripped,user.getBase(),ChatColor.stripColor(e.getValue() == null ? user.getName() : e.getValue()));
                }
            };
            register(essentials_nickname_stripped);
        }, () -> unregister(essentials_nickname_stripped));

        PlayerPlaceholder fly = registerPlayer("fly", p->user(p).getBase().getAllowFlight());
        fly.enableTriggerMode(()->{
            essentials_fly = new Listener() {

                @EventHandler(priority = EventPriority.MONITOR)
                public void onFly(FlyStatusChangeEvent e) {
                    update(fly,e.getAffected().getBase(),e.getValue());
                }
            };
            register(essentials_fly);

        },()->unregister(essentials_fly));
    }

    private User user(TabPlayer p) {
        return ess.getUser(p.getUniqueId());
    }
    private TabPlayer player(IUser p) {
        return p(p.getBase().getUniqueId());
    }


}
