package xyz.qweru.geo.core.game.rotation.processor.impl

import xyz.qweru.geo.client.helper.math.random.RandomProvider
import xyz.qweru.geo.core.game.rotation.data.Rotation
import xyz.qweru.geo.core.game.rotation.processor.RotationProcessor
import kotlin.math.sign

const val maxYawVelocity = 10f
const val yawReduction = 0.5f
const val maxPitchVelocity = 2f
const val pitchReduction = 0.5f

object VelocityRotationProcessor : RotationProcessor {

    private var yVelocity: Float = 0f
        set(value) {
            field = value.coerceIn(-maxYawVelocity, maxYawVelocity)
        }

    private var pVelocity: Float = 0f
        set(value) {
            field = value.coerceIn(-maxPitchVelocity, maxPitchVelocity)
        }

    override fun process(
        start: Rotation,
        end: Rotation,
        current: Rotation,
        delta: Rotation,
        random: RandomProvider
    ): Rotation {
        var yawDelta = delta.yaw
        var pitchDelta = delta.pitch

        if (sign(yVelocity) != sign(yawDelta)) {
            yVelocity += yawDelta * (1 - yawReduction)
            yawDelta = yVelocity
        } else {
            yVelocity += yawDelta
        }

        if (sign(pVelocity) != sign(pitchDelta)) {
            pVelocity += pitchDelta * (1 - pitchReduction)
            pitchDelta = pVelocity
        } else {
            pVelocity += pitchDelta
        }

        yVelocity *= yawReduction
        pVelocity *= pitchReduction

        return Rotation(yawDelta, pitchDelta)
    }
}
