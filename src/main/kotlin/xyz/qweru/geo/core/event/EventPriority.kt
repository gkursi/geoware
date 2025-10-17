package xyz.qweru.geo.core.event

object EventPriority {
    const val FIRST = Int.MAX_VALUE
    const val LAST = Int.MIN_VALUE

    const val HIGHEST = 1000
    const val HIGHER = 500
    const val HIGH = 100
    const val NONE = 0
    const val LOW = -100
    const val LOWER = -500
    const val LOWEST = -1000
}