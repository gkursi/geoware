package xyz.qweru.geo.abstraction.entity

import net.minecraft.network.protocol.game.VecDeltaCodec
import net.minecraft.world.phys.Vec3

class TrackedPosition {
    var pos: Vec3 = Vec3.ZERO

    fun addDelta(dx: Long, dy: Long, dz: Long) {
        pos = VecDeltaCodec().let {
            it.base = pos
            it.decode(dx, dy, dz)
        }
    }
}