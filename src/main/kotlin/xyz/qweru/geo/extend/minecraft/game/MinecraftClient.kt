package xyz.qweru.geo.extend.minecraft.game

import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.player.LocalPlayer

val Minecraft.thePlayer: LocalPlayer
    inline get() = player!!

val Minecraft.theLevel: ClientLevel
    inline get() = level!!