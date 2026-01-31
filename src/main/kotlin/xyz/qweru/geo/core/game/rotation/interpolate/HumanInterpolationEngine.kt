package xyz.qweru.geo.core.game.rotation.interpolate

import xyz.qweru.geo.client.event.GameRenderEvent
import xyz.qweru.geo.core.event.EventBus
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.game.rotation.InterpolationEngine
import xyz.qweru.geo.core.game.rotation.RotationHandler.rotationConfig
import xyz.qweru.geo.core.game.rotation.RotationHandler.random
import xyz.qweru.geo.extend.kotlin.math.inRange
import xyz.qweru.geo.extend.kotlin.math.wrapped
import xyz.qweru.multirender.api.API
import kotlin.math.abs
import kotlin.math.max

object HumanInterpolationEngine : InterpolationEngine {
    private var yawMoved = 0f
    private var yawPenalty = 1f

    private val yaw = Tracker()
    private val pitch = Tracker(0.5f)

    init {
        EventBus.subscribe(this)
    }

    @Handler
    private fun onFrame(e: GameRenderEvent) {
        yawMoved *= 1 - 0.5f * API.base.getDeltaTime()
    }

    override fun stepYaw(start: Float, end: Float, current: Float): Float =
        step(start, end, current, yaw)

    override fun stepPitch(start: Float, end: Float, current: Float): Float =
        step(start, end, current, pitch)

    override fun onYawDelta(delta: Float) = yaw.onDelta(delta)

    override fun onPitchDelta(delta: Float) = pitch.onDelta(delta)

    private fun step(start: Float, end: Float, current: Float, tracker: Tracker): Float {
        val wStart = start.wrapped
        val dist = end.wrapped - wStart
        val progress = abs(current.wrapped - wStart)
        val speed = tracker.getSpeed(progress, abs(dist))
        val mod = random.double(0.1, 1.0) * speed
        return dist * mod.toFloat()
    }

    private class Tracker(private val mul: Float = 1f) {
        private var moved: Float = 0f
        private var penalty: Float = 1f

        fun onDelta(delta: Float) {
            moved += delta

            if (abs(moved) > rotationConfig.mousePadSize) {
                penalty -= rotationConfig.mousePadPenalty * API.base.getDeltaTime()
            }

            if (penalty < rotationConfig.mousePadPenaltyMax) {
                penalty = 1f
                moved = 0f
            }
        }

        fun getSpeed(current: Float, dist: Float): Float {
            var speed = rotationConfig.speed
            if (rotationConfig.micro && rotationConfig.microRange.inRange(dist))
                speed *= 10f
            if (rotationConfig.flick && rotationConfig.flickRange.inRange(dist))
                speed *= rotationConfig.flickBoost
            if (rotationConfig.mousePad)
                speed *= yawPenalty
            if (rotationConfig.speedUp)
                speed *= accelerate(current, dist)
            return speed * mul
        }

        private fun accelerate(current: Float, dist: Float): Float {
            val max = rotationConfig.speedYaw
            val percent = current / max(dist, 1f)

            return if (percent >= max) 2f
            else 1f + percent / max + random.float(-0.1f..0.1f)
        }
    }
}