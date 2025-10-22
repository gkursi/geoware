package xyz.qweru.geo.client.helper.timing

open class Timer {
    private var time: Long = System.nanoTime()
    fun hasPassed(ms: Long) = (System.nanoTime() - time) / 1_000_000 >= ms
    fun timePassed() = (System.nanoTime() - time) / 1_000_000
    fun reset() {
        time = System.nanoTime()
    }
}