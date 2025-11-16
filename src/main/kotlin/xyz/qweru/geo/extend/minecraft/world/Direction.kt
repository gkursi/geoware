package xyz.qweru.geo.extend.minecraft.world

import net.minecraft.core.Direction

val Direction.isVertical: Boolean
    get() = this == Direction.UP || this == Direction.DOWN