package xyz.qweru.geo.client.helper.math.function

interface Function {
    fun getAt(x: Double): Double

    fun getAt(x: Float): Float =
        getAt(x.toDouble()).toFloat()
}