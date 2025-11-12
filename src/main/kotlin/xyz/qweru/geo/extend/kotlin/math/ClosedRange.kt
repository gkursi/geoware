package xyz.qweru.geo.extend.kotlin.math

fun <T: Comparable<T>> ClosedRange<T>.inRange(v: T) = v >= start && v <= endInclusive