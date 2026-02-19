package xyz.qweru.geo.client.helper.math

import xyz.qweru.geo.extend.kotlin.math.wrappedDeg

object RangeHelper {
    fun <T: Comparable<T>> of(x: T, y: T): ClosedRange<T> =
        object : ClosedRange<T> {
            override val start: T = if (y < x) y else x
            override val endInclusive: T = if (x > y) x else y
        }

    fun ofPoint(point: Float, dev: Float, mod: (Float) -> Float = { it }): ClosedRange<Float> =
        of(mod.invoke(point - dev), mod.invoke(point + dev))

    fun ofRotationPoint(point: Float, dev: Float)
        = ofPoint(point, dev) { it.wrappedDeg }
}