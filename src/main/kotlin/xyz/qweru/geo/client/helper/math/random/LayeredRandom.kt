package xyz.qweru.geo.client.helper.math.random

import xyz.qweru.geo.client.helper.math.FloatingPointHelper
import kotlin.math.round

/**
 * Bloat? What's that?
 */
class LayeredRandom : RandomProvider {

    private val layer: Layer = Layer.nop()

    companion object {
        val DEFAULT = LayeredRandom()
            .addLayer(Layer.gaussian(mean = 0.5, deviation = 0.5))
//            .addLayer(Layer.gaussian(weight = 0.5, mean = 0.25, deviation = 0.5))
//            .addLayer(Layer.gaussian(weight = 0.5, mean = -0.25, deviation = 0.5))
            .addLayer(Layer.gaussian(weight = 0.125, mean = 0.5, deviation = 0.5))
//            .addLayer(Layer.java(weight = 0.125))
    }

    override fun next(): Double = layer.get(0.0)

    fun int(range: IntRange) = int(range.start, range.endInclusive)
    fun int(min: Int, max: Int) = round(min + ((max - min) * next())).toInt()

    fun long(range: LongRange) = long(range.start, range.endInclusive)
    fun long(min: Long, max: Long) = round(min + ((max - min) * next())).toLong()

    fun float(range: ClosedRange<Float>) = float(range.start, range.endInclusive)
    fun float(min: Float, max: Float) = min + ((max - min) * next()).toFloat()

    fun double(range: ClosedRange<Double>) = double(range.start, range.endInclusive)
    fun double(min: Double, max: Double) = min + ((max - min) * next())

    fun addLayer(next: Layer): LayeredRandom {
        layer.append(next)
        return this
    }

    data class Layer(val algorithm: RandomProvider, private val weight: Double = 1.0, private var previous: Layer? = null, private var next: Layer? = null) {

        companion object {
            fun nop() = Layer(NOPRandomProvider, weight = 0.0)
            fun java(weight: Double = 1.0) = Layer(JavaRandomProvider(), weight)
            fun gaussian(weight: Double = 1.0, mean: Double = 0.5,deviation: Double = 0.5) =
                Layer(JavaGaussianRandomProvider(mean, deviation), weight)
            fun beta(weight: Double = 1.0, a: Double = 0.0, b: Double = 1.0, bidirectional: Boolean = false) =
                Layer(BetaDistributionProvider(a, b, bidirectional), weight)
            fun gamma(weight: Double = 1.0, shape: Double = 1.0, scale: Double = 2.0) =
                Layer(GammaDistributionProvider(shape, scale), weight)
        }

        fun append(child: Layer) {
            if (this.next == null) {
                this.next = child
                child.previous = this
            } else {
                this.next?.append(child)
            }
        }

        /**
         * If this layer has a child, pass the call on to it.
         * Otherwise, remove this layer from the parent.
         */
        fun removeLast() {
            if (next == null) {
                previous?.next = null // the child of an instances parent is the instance
            } else {
                next?.removeLast()
            }
        }

        fun get(base: Double): Double {
            val change = algorithm.next() * weight
            return FloatingPointHelper.point((next?.get(change) ?: change) + base)
        }
    }
}