package io.github.tanguygab.tabplaceholders.expansions;

import me.neznamy.tab.api.placeholder.Placeholder;
import me.neznamy.tab.api.placeholder.PlayerPlaceholder;
import me.neznamy.tab.shared.TAB;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

import java.util.Locale;

/**
 * Expansion for player stats that does not require any plugin
 *
 * Completed placeholders (3/74):
 * %statistic_mine_block:<material>%
 * %statistic_deaths%
 * %statistic_fly_one_cm%
 *
 *
 * Missing placeholders:
 * %statistic_use_item:<Item Material>%
 * %statistic_break_item:<Item Material>%
 * %statistic_craft_item:<Item Material>%
 * %statistic_kill_entity:<MobType>%
 * %statistic_entity_killed_by:<MobType>%
 * %statistic_mine_block%
 * %statistic_mob_kills%
 * %statistic_use_item%
 * %statistic_break_item%
 * %statistic_craft_item%
 * %statistic_ticks_played%
 * %statistic_seconds_played%
 * %statistic_minutes_played%
 * %statistic_hours_played%
 * %statistic_days_played%
 * %statistic_time_played%
 * %statistic_time_played:seconds%
 * %statistic_time_played:minutes%
 * %statistic_time_played:hours%
 * %statistic_time_played:days%
 * %statistic_animals_bred%
 * %statistic_armor_cleaned%
 * %statistic_banner_cleaned%
 * %statistic_beacon_interacted%
 * %statistic_boat_one_cm%
 * %statistic_brewingstand_interaction%
 * %statistic_cake_slices_eaten%
 * %statistic_cauldron_filled%
 * %statistic_cauldron_used%
 * %statistic_chest_opened%
 * %statistic_climb_one_cm%
 * %statistic_crafting_table_interaction%
 * %statistic_crouch_one_cm%
 * %statistic_damage_dealt%
 * %statistic_damage_taken%
 * %statistic_dispenser_inspected%
 * %statistic_dive_one_cm%
 * %statistic_drop%
 * %statistic_dropper_inspected%
 * %statistic_enderchest_opened%
 * %statistic_fall_one_cm%
 * %statistic_fish_caught%
 * %statistic_flower_potted%
 * %statistic_furnace_interaction%
 * %statistic_hopper_inspected%
 * %statistic_horse_one_cm%
 * %statistic_item_enchanted%
 * %statistic_jump%
 * %statistic_junk_fished%
 * %statistic_leave_game%
 * %statistic_minecart_one_cm%
 * %statistic_noteblock_played%
 * %statistic_noteblock_tuned%
 * %statistic_pig_one_cm%
 * %statistic_player_kills%
 * %statistic_record_played%
 * %statistic_sprint_one_cm%
 * %statistic_swim_one_cm%
 * %statistic_talked_to_villager%
 * %statistic_time_since_death%
 * %statistic_ticks_since_death%
 * %statistic_seconds_since_death%
 * %statistic_minutes_since_death%
 * %statistic_hours_since_death%
 * %statistic_days_since_death%
 * %statistic_traded_with_villager%
 * %statistic_trapped_chest_triggered%
 * %statistic_walk_one_cm%
 * %statistic_sleep_in_bed%
 * %statistic_sneak_time%
 * %statistic_aviate_one_cm%
 */
public class StatisticExpansion extends Expansion {

    public StatisticExpansion(Plugin plugin) {
        super(plugin, "statistic");
    }

    @Override
    public void registerPlaceholders() {
        statistic_deaths();
        statistic_fly_one_cm();
    }

    private void statistic_deaths() {
        PlayerPlaceholder deaths = registerPlayer("deaths", p -> p(p).getStatistic(Statistic.DEATHS));
        Listener listener = new Listener() {

            @EventHandler(priority = EventPriority.MONITOR)
            public void onDeath(PlayerDeathEvent e) {
                update(deaths, e.getEntity(), e.getEntity().getStatistic(Statistic.DEATHS)+1);
            }
        };
        deaths.enableTriggerMode(() -> register(listener), () -> unregister(listener));
    }

    private void statistic_fly_one_cm() {
        PlayerPlaceholder fly_one_cm = registerPlayer("fly_one_cm", p -> p(p).getStatistic(Statistic.FLY_ONE_CM));
        Listener listener = new Listener() {

            @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
            public void onMove(PlayerMoveEvent e) {
                if (!e.getPlayer().isOnGround()) {
                    update(fly_one_cm, e.getPlayer(), e.getPlayer().getStatistic(Statistic.FLY_ONE_CM));
                }
            }
        };
        fly_one_cm.enableTriggerMode(() -> register(listener), () -> unregister(listener));
    }

    private PlayerPlaceholder statistic_mine_block_BLOCK(String identifier) {
        String block = identifier.substring(22, identifier.length()-1);
        try {
            Material m = Material.valueOf(block.toUpperCase(Locale.US));
            PlayerPlaceholder mineBlock = registerPlayer("mine_block:" + block, p -> p(p).getStatistic(Statistic.MINE_BLOCK, m));
            Listener listener = new Listener() {

                @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
                public void onBlockBreak(BlockBreakEvent e) {
                    if (e.getBlock().getType() == m && e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
                        update(mineBlock, e.getPlayer(), e.getPlayer().getStatistic(Statistic.MINE_BLOCK, m)+1);
                    }
                }
            };
            mineBlock.enableTriggerMode(() -> register(listener), () -> unregister(listener));
            return mineBlock;
        } catch (IllegalArgumentException ex) {
            TAB.getInstance().getErrorManager().printError("\"" + block + "\" is not a valid material for %statistic_mine_block:<Block>% placeholder");
            return registerPlayer("mine_block:" + block, p -> "Invalid material " + block + "");
        }
    }

    public Placeholder onPlaceholderRegister(String identifier) {
        if (identifier.startsWith("%statistic_mine_block:")) {
            return statistic_mine_block_BLOCK(identifier);
        }
        return null;
    }
}
