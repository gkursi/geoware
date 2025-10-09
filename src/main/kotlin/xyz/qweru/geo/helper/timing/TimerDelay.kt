package xyz.qweru.geo.helper.timing

import xyz.qweru.geo.client.setting.DelaySetting
import java.util.Random

class TimerDelay : Timer() {
    var target = 0L
    val random = Random()

    fun hasPassed(): Boolean = hasPassed(target)

    fun reset(min: Long, max: Long) {
        target = if (min == max) min else random.nextLong(min, max)
        reset()
    }

    fun reset(delay: DelaySetting.Delay) = reset(delay.min, delay.max)
}