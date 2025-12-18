package xyz.qweru.geo.client.helper.math.random

import org.apache.commons.math3.distribution.GammaDistribution
import xyz.qweru.geo.client.helper.math.RandomProvider

class GammaDistributionProvider(shape: Double = 2.0, scale: Double = 3.0) : RandomProvider {
    val distribution by lazy { GammaDistribution(shape, scale) }
    override fun next(): Double = distribution.sample()
}