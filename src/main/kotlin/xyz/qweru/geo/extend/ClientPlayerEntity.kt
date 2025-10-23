package xyz.qweru.geo.extend

import net.minecraft.client.network.ClientPlayerEntity
import xyz.qweru.geo.imixin.IClientPlayerEntity

val ClientPlayerEntity.groundTicks: Int
    get() = (this as IClientPlayerEntity).geo_getGroundTicks()
val ClientPlayerEntity.airTicks: Int
    get() = (this as IClientPlayerEntity).geo_getAirTicks()