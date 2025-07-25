package org.everbuild.celestia.orion.core.scoreboard

import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player

interface ScoreBoardCallback {
    /**
     * Get The Key-Value Pair for a Scoreboard entry
     *
     * @param player The Player, the Scoreboard is generated for
     * @param id The number of the scoreboard entry [0,1,2,3,4]
     * @return [Key, Prefix, Suffix]
     */
    fun getKV(player: Player, id: Int): Array<Component>

    /**
     * Get the Scoreboard Title
     *
     * @param player The Player, the Scoreboard is generated for
     * @return The Title for the Scoreboard
     */
    fun getTitle(player: Player): Component {
        return Component.text("ScoreBoard")
    }

    fun getExtra(player: Player): Component = Component.text("")

    /**
     * Should the ScoreBoard be showed
     *
     * @param player The Player, the Scoreboard is generated for
     * @return The Display Status
     */
    fun active(player: Player): Boolean
}