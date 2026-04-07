package xyz.qweru.geo.core.game.rotation.processor.impl

import xyz.qweru.geo.client.helper.math.random.RandomProvider
import xyz.qweru.geo.core.game.rotation.data.Rotation
import xyz.qweru.geo.core.game.rotation.processor.RotationProcessor
import kotlin.math.abs

class StopRotationProcessor(val yawLimit: Float, val reset: Float) : RotationProcessor {
    var yawMoved: Float = 0f

    override fun process(
        start: Rotation,
        end: Rotation,
        current: Rotation,
        delta: Rotation,
        random: RandomProvider
    ): Rotation {
        yawMoved += delta.yaw

        if (abs(yawMoved) > yawLimit) {
            yawMoved -= reset

            if (yawMoved < yawLimit) {
                yawMoved = 0f
            }

            return delta.copy(yaw = 0f)
        } else {
            return delta
        }
    }
}