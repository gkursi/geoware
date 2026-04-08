package xyz.qweru.geo.client.module.move

import xyz.qweru.geo.client.event.PostMoveSendEvent
import xyz.qweru.geo.client.event.PostMovementTickEvent
import xyz.qweru.geo.client.event.TravelEvent
import xyz.qweru.geo.client.helper.network.PacketHelper
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.impl.module.Category
import xyz.qweru.geo.core.system.impl.module.Module

class ModuleLimiter : Module("Limiter", "Hard cap movement speed", Category.MOVEMENT) {
    private val sg = settings.general
    private val limit by sg.float("Limit", "Movement speed limit", 0.0006f, 0f, 6f)
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