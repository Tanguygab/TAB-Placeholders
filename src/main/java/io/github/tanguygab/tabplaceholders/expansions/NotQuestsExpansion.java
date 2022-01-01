package io.github.tanguygab.tabplaceholders.expansions;

import me.neznamy.tab.api.placeholder.PlayerPlaceholder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import rocks.gravili.notquests.Events.notquests.QuestPointsChangeEvent;
import rocks.gravili.notquests.NotQuests;

/**
 * Expansion for plugin NotQuests
 * Link: https://www.spigotmc.org/resources/95872/
 *
 * Completed placeholders (1/10):
 * %notquests_player_questpoints%
 */
public class NotQuestsExpansion extends Expansion {

    public NotQuestsExpansion(Plugin plugin) {
        super(plugin,"notquests");
    }

    @Override
    public void registerPlaceholders() {
        notquests_player_questpoints();
    }

    private void notquests_player_questpoints() {
        PlayerPlaceholder questPoints = registerPlayer("player_questpoints",
                p -> NotQuests.getInstance().getQuestPlayerManager().getQuestPlayer(p.getUniqueId()) == null ? 0 :
                        NotQuests.getInstance().getQuestPlayerManager().getQuestPlayer(p.getUniqueId()).getQuestPoints());
        Listener listener = new Listener() {

            @EventHandler(priority = EventPriority.MONITOR)
            public void onQuestPointsChange(QuestPointsChangeEvent e) {
                questPoints.updateValue(p(e.getQuestPlayer().getUUID()), e.getNewQuestPointsAmount());
            }
        };
        questPoints.enableTriggerMode(() -> register(listener), () -> unregister(listener));
    }
}