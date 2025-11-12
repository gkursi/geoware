package xyz.qweru.geo.abstraction.network

import net.minecraft.network.Connection
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ServerPacketListener
import xyz.qweru.geo.core.Global
import xyz.qweru.geo.imixin.IConnection

object GConnection {
    private val connection: Connection?
        get() = Global.mc.connection?.connection

    fun sendPacketImmediately(packet: Packet<out ServerPacketListener>) =
        connection?.let { (it as IConnection).geo_doSend(packet) }

    fun sendPacket(packet: Packet<out ServerPacketListener>) =
        connection?.send(packet)
}