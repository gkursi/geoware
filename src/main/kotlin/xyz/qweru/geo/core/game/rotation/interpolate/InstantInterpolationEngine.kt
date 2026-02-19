package xyz.qweru.geo.core.game.rotation.interpolate

import xyz.qweru.geo.core.game.rotation.InterpolationEngine

object InstantInterpolationEngine : InterpolationEngine {
    override fun stepYaw(start: Float, end: Float, current: Float): Float = 36000f
    override fun stepPitch(start: Float, end: Float, current: Float): Float = 18000f
}