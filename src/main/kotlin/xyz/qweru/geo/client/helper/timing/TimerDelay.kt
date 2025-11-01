package xyz.qweru.geo.client.helper.timing

import xyz.qweru.geo.client.helper.math.random.LayeredRandom
import xyz.qweru.geo.client.setting.range.LongRangeSetting
import java.util.Random

class TimerDelay : Timer() {
    var target = 0L
    val random = LayeredRandom.DEFAULT

    fun hasPassed(): Boolean = hasPassed(target)

    fun reset(min: Long, max: Long) {
        target = if (min == max) min else random.long(min, max)
        reset()
    }

    fun reset(delay: LongRange) = reset(delay.start, delay.endInclusive + 1L)
}