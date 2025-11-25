package xyz.qweru.geo.core.game.rotation.interpolate

import xyz.qweru.geo.core.game.rotation.InterpolationEngine
import kotlin.math.abs

object InstantInterpolationEngine : InterpolationEngine {
    override fun step(start: Float, end: Float): Float = abs(end - start)
}