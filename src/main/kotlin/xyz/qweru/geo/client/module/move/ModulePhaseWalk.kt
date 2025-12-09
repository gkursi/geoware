package xyz.qweru.geo.client.module.move

import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket
import xyz.qweru.geo.client.event.PacketReceiveEvent
import xyz.qweru.geo.client.event.PostMovementTickEvent
import xyz.qweru.geo.client.helper.math.FloatingPointHelper
import xyz.qweru.geo.client.helper.math.RangeHelper
import xyz.qweru.geo.client.helper.network.PacketHelper
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.kotlin.math.inRange
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import kotlin.math.abs

class ModulePhaseWalk : Module("PhaseWalk", "Clip trough blocks on dupeanarchy", Category.MOVEMENT) {
    @Handler
    private fun postMovement(e: PostMovementTickEvent) {
        if (!onEdge() || !canClip())
            return
        PacketHelper.moveBy(y = -8.0) // clip down
        PacketHelper.moveBy(x = 1.0, z = 1.0) // rubberband
    }

    @Handler
    private fun receivePacket(e: PacketReceiveEvent) {
        val packet = e.packet
        if (!inGame || packet !is ClientboundPlayerPositionPacket || packet.change.position.y > -64) return
    }

    private fun onEdge(): Boolean {
        val pointX = FloatingPointHelper.point(abs(mc.thePlayer.x))
        val pointZ = FloatingPointHelper.point(abs(mc.thePlayer.z))
        return RangeHelper.from(0.29, 0.31).let {
            it.inRange(pointX) || it.inRange(pointZ)
        } || RangeHelper.from(0.69, 0.71).let {
            it.inRange(pointX) || it.inRange(pointZ)
        }
    }

    private fun canClip() = mc.thePlayer.horizontalCollision
}