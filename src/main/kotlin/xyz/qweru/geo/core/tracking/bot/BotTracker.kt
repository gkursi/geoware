package xyz.qweru.geo.core.tracking.bot

import net.minecraft.world.entity.player.Player
import xyz.qweru.geo.client.module.config.ModuleTarget
import xyz.qweru.geo.core.tracking.PlayerTracker
import xyz.qweru.geo.core.system.SystemCache

object BotTracker : PlayerTracker {
    val config: ModuleTarget by SystemCache.getModule()

    init {
        BotType.register()
    }

    override fun isTracking(player: Player): Boolean {
        for (bot in config.botModes.getEnabled()) {
            if (bot.tracker.isTracking(player)) return true
        }

        return false
    }
}