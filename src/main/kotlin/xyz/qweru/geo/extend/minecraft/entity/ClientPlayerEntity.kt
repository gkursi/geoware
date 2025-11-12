package xyz.qweru.geo.extend.minecraft.entity

import net.minecraft.client.player.LocalPlayer
import xyz.qweru.geo.imixin.ILocalPlayer

val LocalPlayer.groundTicks: Int
    get() = (this as ILocalPlayer).geo_getGroundTicks()
val LocalPlayer.airTicks: Int
    get() = (this as ILocalPlayer).geo_getAirTicks()