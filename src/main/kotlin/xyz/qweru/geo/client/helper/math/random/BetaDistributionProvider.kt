package xyz.qweru.geo.client.helper.math.random

import org.apache.commons.math3.distribution.BetaDistribution
import xyz.qweru.geo.client.helper.math.RandomProvider

class BetaDistributionProvider(a: Double, b: Double, val bidirectional: Boolean) : RandomProvider {
    val length = (b - a) * .5
    val distribution by lazy { BetaDistribution(a, b) }
    override fun next(): Double = distribution.sample() + if (bidirectional) length else 0.0
}