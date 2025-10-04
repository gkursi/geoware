package xyz.qweru.geo.helper

import java.util.Random

class TimerDelay : Timer() {
    var target = 0L
    val random = Random()

    fun hasPassed(): Boolean = hasPassed(target)

    fun reset(min: Long, max: Long) {
        target = if (min == max) min else random.nextLong(min, max)
        reset()
    }
}