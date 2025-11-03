package xyz.qweru.geo.extend

fun <T: Comparable<T>> ClosedRange<T>.inRange(v: T) = v >= start && v <= endInclusive