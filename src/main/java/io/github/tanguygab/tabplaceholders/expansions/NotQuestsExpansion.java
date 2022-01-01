package io.github.tanguygab.tabplaceholders.expansions;

import me.neznamy.tab.api.placeholder.PlayerPlaceholder;
import me.neznamy.tab.shared.TAB;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import rocks.gravili.notquests.paper.events.notquests.QuestCompletedEvent;
import rocks.gravili.notquests.paper.events.notquests.QuestPointsChangeEvent;
import rocks.gravili.notquests.paper.NotQuests;

public class NotQuestsExpansion extends Expansion {

    private Listener notquests_questpoints;
    private Listener notquests_completedquest;


    public NotQuestsExpansion(Plugin plugin) {
        super(plugin,"notquests");
    }

    /**
     * Plugin URL: https://www.spigotmc.org/resources/1-17-1-notquests-%E2%9A%A1-solid-quest-system-%E2%9C%85.95872/
     *
     * DONE:
     * %notquests_player_questpoints%
     */
    @Override
    public void registerPlaceholders() {
        NotQuests notQuests = NotQuests.getInstance();



        PlayerPlaceholder questPoints = registerPlayer("player_questpoints", p -> notQuests.getQuestPlayerManager().getQuestPlayer(p.getUniqueId()) == null ? 0 : notQuests.getQuestPlayerManager().getQuestPlayer(p.getUniqueId()).getQuestPoints());
        questPoints.enableTriggerMode(() -> {
            notquests_questpoints = new Listener() {

                @EventHandler(priority = EventPriority.MONITOR)
                public void onQuestPointsChange(QuestPointsChangeEvent e) {
                    questPoints.updateValue(TAB.getInstance().getPlayer(e.getQuestPlayer().getUUID()), ""+e.getNewQuestPointsAmount() );
                }
            };
            register(notquests_questpoints);
        }, () -> unregister(notquests_questpoints));


        PlayerPlaceholder completedQuestsAmount = registerPlayer("player_completed_quests_amount", p -> notQuests.getQuestPlayerManager().getQuestPlayer(p.getUniqueId()) == null ? 0 : notQuests.getQuestPlayerManager().getQuestPlayer(p.getUniqueId()).getCompletedQuests().size());
        completedQuestsAmount.enableTriggerMode(() -> {
            notquests_completedquest = new Listener() {

                @EventHandler(priority = EventPriority.MONITOR)
                public void onQuestCompleted(QuestCompletedEvent e) {
                    completedQuestsAmount.updateValue(TAB.getInstance().getPlayer(e.getQuestPlayer().getUUID()), ""+e.getQuestPlayer().getCompletedQuests().size() );
                }
            };
            register(notquests_completedquest);
        }, () -> unregister(notquests_completedquest));

    }
}