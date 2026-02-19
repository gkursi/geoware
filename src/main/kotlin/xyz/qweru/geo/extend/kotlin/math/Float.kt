package xyz.qweru.geo.extend.kotlin.math

import net.minecraft.util.Mth

val Float.wrappedDeg: Float
    get() = Mth.wrapDegrees(this)