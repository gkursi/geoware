package xyz.qweru.geo.client.helper.network

import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.network.Connection
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ServerPacketListener
import xyz.qweru.geo.core.Core
import xyz.qweru.geo.imixin.IConnection

object ClientConnection {
    private val networkHandler: ClientPacketListener?
        get() = Core.mc.connection

    private val connection: Connection?
        get() = networkHandler?.connection


    fun sendPacketImmediately(packet: Packet<out ServerPacketListener>) =
        connection?.let { (it as IConnection).geo_doSend(packet) }

    fun sendPacket(packet: Packet<out ServerPacketListener>) =
        connection?.send(packet)

    fun playerList() = networkHandler?.onlinePlayers
}