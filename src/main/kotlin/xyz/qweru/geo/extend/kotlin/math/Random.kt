package xyz.qweru.geo.extend.kotlin.math

import kotlin.random.Random

fun Random.nextFloat(min: Float, max: Float) =
    min + nextFloat() * (max - min)