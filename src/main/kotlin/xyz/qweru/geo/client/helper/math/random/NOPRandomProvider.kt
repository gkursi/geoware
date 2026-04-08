package xyz.qweru.geo.client.helper.math.random

import xyz.qweru.geo.client.helper.math.random.RandomProvider

object NOPRandomProvider : RandomProvider {
    override fun next() = 0.0
}