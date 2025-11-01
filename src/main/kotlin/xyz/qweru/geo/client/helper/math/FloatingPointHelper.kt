package xyz.qweru.geo.client.helper.math

object FloatingPointHelper {
    fun point(double: Double): Double = double - double.toInt()
    fun point(float: Float): Float = float - float.toInt()
}