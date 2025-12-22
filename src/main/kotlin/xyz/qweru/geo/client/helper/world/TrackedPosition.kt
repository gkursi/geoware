package xyz.qweru.geo.client.helper.world

import net.minecraft.network.protocol.game.VecDeltaCodec
import net.minecraft.world.phys.Vec3
import xyz.qweru.geo.core.Core.mc
import xyz.qweru.geo.extend.minecraft.entity.pos
import xyz.qweru.geo.extend.minecraft.game.thePlayer

class TrackedPosition {
    var pos: Vec3 = Vec3.ZERO

    fun addDelta(dx: Long, dy: Long, dz: Long) {
        pos = VecDeltaCodec().let {
            it.base = pos
            it.decode(dx, dy, dz)
        }
    }

    fun inRange(range: Float): Boolean =
        pos.distanceToSqr(mc.thePlayer.pos) <= range * range
}

fun TrackedPosition?.inRange(range: Float): Boolean =
    this?.inRange(range) ?: false