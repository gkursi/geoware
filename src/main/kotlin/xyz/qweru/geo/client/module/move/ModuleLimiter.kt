package xyz.qweru.geo.client.module.move

import xyz.qweru.geo.client.event.PacketReceiveEvent
import xyz.qweru.geo.client.event.PostMoveSendEvent
import xyz.qweru.geo.client.event.PostMovementTickEvent
import xyz.qweru.geo.client.event.TravelEvent
import xyz.qweru.geo.client.helper.math.FloatingPointHelper
import xyz.qweru.geo.client.helper.math.RangeHelper
import xyz.qweru.geo.client.helper.network.PacketHelper
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.kotlin.math.inRange
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import kotlin.math.abs

class ModuleLimiter : Module("Limiter", "Hard cap movement speed", Category.MOVEMENT) {
    private val sg = settings.group("General")
    private val limit by sg.float("Limit", "Movement speed limit", 0.006f, 0f, 6f)
    private val reset by sg.boolean("Reset", "Reset position delta after moving", false)

    @Handler
    private fun postMovement(e: PostMovementTickEvent) {
        e.clamp(limit.toDouble())
    }

    @Handler
    private fun postMovement(e: TravelEvent) {
        e.clamp(limit.toDouble())
    }

    @Handler
    private fun postPackets(e: PostMoveSendEvent) {
        if (!reset) return
        PacketHelper.moveBy(y = 6767.0)
    }
}