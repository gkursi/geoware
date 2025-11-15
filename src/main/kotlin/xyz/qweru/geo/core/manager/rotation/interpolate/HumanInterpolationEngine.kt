package xyz.qweru.geo.core.manager.rotation.interpolate

import net.minecraft.util.Mth
import xyz.qweru.geo.core.manager.rotation.InterpolationEngine
import xyz.qweru.geo.core.manager.rotation.RotationHandler.rotationConfig
import xyz.qweru.geo.core.manager.rotation.RotationHandler.random
import xyz.qweru.geo.extend.kotlin.math.inRange
import xyz.qweru.multirender.api.API
import kotlin.math.abs

object HumanInterpolationEngine : InterpolationEngine {
    private var yawMoved = 0f
    private var yawPenalty = 1f

    override fun step(start: Float, end: Float): Float {
        val min = Mth.wrapDegrees(start)
        val dist = Mth.wrapDegrees(end) - min
        val speed = getSpeed(dist)
        val mod = random.double(0.1, 1.0) * speed
        return dist * mod.toFloat()
    }

    override fun onYawDelta(delta: Float) {
        yawMoved += delta

        if (abs(yawMoved) > rotationConfig.mousePadSize) {
            yawPenalty -= rotationConfig.mousePadPenalty * API.base.getDeltaTime()
        }

        if (yawPenalty < rotationConfig.mousePadPenaltyMax) {
            yawPenalty = 1f
            yawMoved = 0f
        }
    }

    private fun getSpeed(dist: Float): Float {
        var speed = rotationConfig.speed
        if (rotationConfig.micro && rotationConfig.microRange.inRange(dist))
            speed *= 10f
        if (rotationConfig.flick && rotationConfig.flickRange.inRange(dist))
            speed *= rotationConfig.flickBoost
        if (rotationConfig.mousePad)
            speed *= yawPenalty
        return speed
    }

}