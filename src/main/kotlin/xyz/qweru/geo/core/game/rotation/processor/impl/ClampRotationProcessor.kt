package xyz.qweru.geo.core.game.rotation.processor.impl

import xyz.qweru.geo.core.game.rotation.data.Rotation
import xyz.qweru.geo.core.game.rotation.processor.RotationProcessor

class ClampRotationProcessor : RotationProcessor {
    override fun process(
        start: Rotation,
        end: Rotation,
        current: Rotation,
        delta: Rotation
    ): Rotation {
        // todo: overshoot
        TODO()
    }

//    fun clamp(current: Float, delta: Float, max: Float) {
//        Mth.wrapDegrees()
//    }
}