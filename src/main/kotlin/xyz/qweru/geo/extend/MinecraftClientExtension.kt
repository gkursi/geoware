package xyz.qweru.geo.extend

import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.world.ClientWorld

val MinecraftClient.thePlayer: ClientPlayerEntity
    inline get() = player!!

val MinecraftClient.theWorld: ClientWorld
    inline get() = world!!