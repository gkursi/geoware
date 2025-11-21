package xyz.qweru.geo.core.tracking.bot.trackers

import net.minecraft.world.entity.player.Player
import xyz.qweru.geo.core.tracking.PlayerTracker
import xyz.qweru.geo.extend.minecraft.entity.playerListEntry
import xyz.qweru.geo.mixin.entity.PlayerInfoAccessor
import java.util.regex.Pattern
import kotlin.math.abs

object SimpleBotTracker : PlayerTracker {
    private val invalid = Pattern.compile("^(?![A-Za-z0-9_]{2,16}$).+$")!!

    override fun isTracking(player: Player): Boolean {
        val info = player.playerListEntry ?: return true
        val ping = (info as PlayerInfoAccessor).geo_getLatency()
        return ping < 0 || player.tickCount > 2000 && ping == 0
            || invalid.matcher(player.gameProfile.name).matches()
            || info.gameMode == null
            || abs(player.xRot) > 90
    }
}