package xyz.qweru.geo.client.helper.math.random

import xyz.qweru.geo.client.helper.math.RandomProvider
import java.util.Random

/**
 * Not thread safe
 */
class JavaGaussianRandomProvider(val mean: Double, val deviation: Double) : RandomProvider {
    val random = Random()
    override fun next(): Double = random.nextGaussian(mean, deviation)
}