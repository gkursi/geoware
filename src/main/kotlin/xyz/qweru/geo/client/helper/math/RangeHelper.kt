package xyz.qweru.geo.client.helper.math

object RangeHelper {
    fun <T: Comparable<T>> from(x: T, y: T): ClosedRange<T> =
        object : ClosedRange<T> {
            override val start: T = x
            override val endInclusive: T = y
        }

    fun fromPoint(point: Float, dev: Float): ClosedRange<Float> =
        from(point - dev, point + dev)
}