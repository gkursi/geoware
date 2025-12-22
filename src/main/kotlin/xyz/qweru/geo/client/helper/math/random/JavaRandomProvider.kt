package xyz.qweru.geo.client.helper.math.random

import xyz.qweru.geo.client.helper.math.RandomProvider
import java.util.Random

/**
 * Not thread safe
 */
class JavaRandomProvider : RandomProvider {
    private val random = Random()
    override fun next(): Double = random.nextDouble()
}