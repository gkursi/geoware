package xyz.qweru.geo.client.helper.math

object RangeHelper {
    fun <T: Comparable<T>> rangeOf(x: T, y: T): ClosedRange<T> =
        object : ClosedRange<T> {
            override val start: T = x
            override val endInclusive: T = y
        }
}