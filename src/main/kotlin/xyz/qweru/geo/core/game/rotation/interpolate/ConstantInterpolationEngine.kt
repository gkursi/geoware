package xyz.qweru.geo.core.game.rotation.interpolate

import xyz.qweru.geo.client.helper.math.random.LayeredRandom
import xyz.qweru.geo.client.module.config.ModuleRotation
import xyz.qweru.geo.core.game.rotation.InterpolationEngine
import xyz.qweru.geo.core.system.SystemCache

object ConstantInterpolationEngine : InterpolationEngine {
    private val config by SystemCache.getModule<ModuleRotation>()
    private val random = LayeredRandom.DEFAULT

    override fun step(start: Float, end: Float): Float =
        config.step * (1 + random.float(-1f..1f) * config.offset)
}