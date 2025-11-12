package xyz.qweru.geo.client.helper.math

import net.minecraft.util.Mth

object RangeHelper {
    fun <T: Comparable<T>> from(x: T, y: T): ClosedRange<T> =
        object : ClosedRange<T> {
            override val start: T = x
            override val endInclusive: T = y
        }

    fun fromPoint(point: Float, dev: Float, mod: (Float) -> Float = { it }): ClosedRange<Float> =
        from(mod.invoke(point - dev), mod.invoke(point + dev))

    fun fromRotationPoint(point: Float, dev: Float)
        = fromPoint(point, dev, mod = { Mth.wrapDegrees(it) })
}