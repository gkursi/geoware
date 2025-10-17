package xyz.qweru.geo.extend

import net.minecraft.entity.Entity
import xyz.qweru.geo.imixin.IEntity

val Entity.groundTicks: Int
    get() = (this as IEntity).geo_getGroundTicks()