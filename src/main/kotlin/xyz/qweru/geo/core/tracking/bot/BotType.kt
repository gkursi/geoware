package xyz.qweru.geo.core.tracking.bot

import xyz.qweru.geo.core.event.EventBus
import xyz.qweru.geo.core.tracking.PlayerTracker
import xyz.qweru.geo.core.tracking.bot.trackers.MatrixBotTracker
import xyz.qweru.geo.core.tracking.bot.trackers.SimpleBotTracker

@Suppress("UNUSED")
enum class BotType(val tracker: PlayerTracker) {
    SIMPLE(SimpleBotTracker),
    MATRIX(MatrixBotTracker);

    companion object {
        fun register() =
            entries.forEach { EventBus.subscribe(it.tracker) }
    }
}