package xyz.qweru.geo.client.helper.math.random

object NOPRandomProvider : RandomProvider {
    override fun next() = 0.0
}