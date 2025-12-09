package xyz.qweru.geo.extend.kotlin.math

import net.minecraft.util.Mth

val Float.wrapped: Float
    get() = Mth.wrapDegrees(this)