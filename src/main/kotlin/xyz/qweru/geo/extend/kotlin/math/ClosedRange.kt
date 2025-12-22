@file:Suppress("NOTHING_TO_INLINE")
package xyz.qweru.geo.extend.kotlin.math

inline fun <T: Comparable<T>> ClosedRange<T>.inRange(v: T) = v in start..endInclusive // i somehow missed contains when writing this
inline val <T : Comparable<T>> ClosedRange<T>.reversedIfEmpty get() = if (isEmpty()) endInclusive..start else this
inline fun <T : Comparable<T>> ClosedRange<T>.clamp(range: ClosedRange<T>?) = if (range === null) this else start.coerceIn(range)..endInclusive.coerceIn(range)
inline fun <T : Comparable<T>> T.clamp(range: ClosedRange<T>?) = if (range === null) this else coerceIn(range)
