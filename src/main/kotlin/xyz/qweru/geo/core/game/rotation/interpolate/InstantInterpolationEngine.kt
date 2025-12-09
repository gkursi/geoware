package xyz.qweru.geo.core.game.rotation.interpolate

import xyz.qweru.geo.core.game.rotation.InterpolationEngine
import xyz.qweru.geo.extend.kotlin.math.wrapped
import kotlin.math.abs

object InstantInterpolationEngine : InterpolationEngine {
    override fun stepYaw(start: Float, end: Float, current: Float): Float = abs(end.wrapped - start.wrapped)
    override fun stepPitch(start: Float, end: Float, current: Float): Float = abs(end.wrapped - start.wrapped)
}