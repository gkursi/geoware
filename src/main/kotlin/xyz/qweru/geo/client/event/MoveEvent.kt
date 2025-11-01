package xyz.qweru.geo.client.event

import xyz.qweru.geo.core.Global.mc
import xyz.qweru.geo.extend.thePlayer
import xyz.qweru.geo.mixin.math.Vec3dAccesor
import kotlin.math.sqrt

object PreMovementTickEvent
object PostMovementTickEvent {
    var velX = 0.0
        set(value) {
            field = value
            (mc.thePlayer.velocity as Vec3dAccesor).geo_setX(value)
        }
    var velY = 0.0
        set(value) {
            field = value
            (mc.thePlayer.velocity as Vec3dAccesor).geo_setY(value)
        }
    var velZ = 0.0
        set(value) {
            field = value
            (mc.thePlayer.velocity as Vec3dAccesor).geo_setZ(value)
        }

    fun clampHorizontal(maxMag: Double) {
        val magnitude = sqrt(velX * velX + velZ * velZ)
        if (magnitude > maxMag) {
            velX = (velX / magnitude) * maxMag
            velZ = (velZ / magnitude) * maxMag
        }
    }
}

object ForwardMovementCheckEvent {
    var hasForwardMovement = false
}

object PostInputTick