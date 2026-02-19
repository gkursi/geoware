package xyz.qweru.geo.core.game.rotation.processor.impl

import xyz.qweru.geo.core.game.rotation.processor.RotationProcessor
import xyz.qweru.geo.core.game.rotation.data.Rotation
import xyz.qweru.geo.extend.kotlin.math.random

class FactorRotationProcessor(val factorRange: ClosedRange<Float>) : RotationProcessor {
    override fun process(
        start: Rotation,
        end: Rotation,
        current: Rotation,
        delta: Rotation
    ): Rotation {
        return Rotation(
            delta.yaw * factorRange.random(),
            delta.pitch * factorRange.random()
        )
    }
}