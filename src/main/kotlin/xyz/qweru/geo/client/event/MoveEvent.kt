package xyz.qweru.geo.client.event

import xyz.qweru.geo.core.Glob.mc
import xyz.qweru.geo.extend.thePlayer
import xyz.qweru.geo.mixin.game.Vec3dAccesor

object VelocityTickEvent {
    var x = 0.0
        set(value) {
            field = value
            (mc.thePlayer.velocity as Vec3dAccesor).geo_setX(value)
        }
    var y = 0.0
        set(value) {
            field = value
            (mc.thePlayer.velocity as Vec3dAccesor).geo_setY(value)
        }
    var z = 0.0
        set(value) {
            field = value
            (mc.thePlayer.velocity as Vec3dAccesor).geo_setZ(value)
        }
}