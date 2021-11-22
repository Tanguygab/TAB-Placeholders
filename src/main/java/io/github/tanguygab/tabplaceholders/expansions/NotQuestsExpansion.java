package io.github.tanguygab.tabplaceholders.expansions;

import com.earth2me.essentials.Essentials;
import me.neznamy.tab.api.placeholder.PlaceholderManager;
import me.neznamy.tab.api.placeholder.PlayerPlaceholder;
import me.neznamy.tab.shared.TAB;
import net.ess3.api.events.AfkStatusChangeEvent;
import net.ess3.api.events.GodStatusChangeEvent;
import net.ess3.api.events.NickChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import rocks.gravili.notquests.Events.notquests.QuestPointsChangeEvent;
import rocks.gravili.notquests.NotQuests;

public class NotQuestsExpansion extends Expansion {

    private Listener notquests_questpoints;


    public NotQuestsExpansion(Plugin plugin) {
        super(plugin);
    }

    /**
     * Plugin URL: https://www.spigotmc.org/resources/1-17-1-notquests-%E2%9A%A1-solid-quest-system-%E2%9C%85.95872/
     *
     * DONE:
     * %notquests_player_questpoints%
     */
    @Override
    public void registerPlaceholders() {
        PlaceholderManager manager = TAB.getInstance().getPlaceholderManager();


        NotQuests notQuests = NotQuests.getInstance();

        PlayerPlaceholder questPoints = manager.registerPlayerPlaceholder("%notquests_player_questpoints%", -1, p -> notQuests.getQuestPlayerManager().getQuestPlayer(p.getUniqueId()) == null ? 0 : notQuests.getQuestPlayerManager().getQuestPlayer(p.getUniqueId()).getQuestPoints());
        questPoints.enableTriggerMode(() -> {
            notquests_questpoints = new Listener() {

                @EventHandler(priority = EventPriority.MONITOR)
                public void onNickChange(QuestPointsChangeEvent e) {
                    questPoints.updateValue(TAB.getInstance().getPlayer(e.getQuestPlayer().getUUID()), ""+e.getNewQuestPointsAmount() );
                }
            };
            register(notquests_questpoints);
        }, () -> unregister(notquests_questpoints));

    }
}