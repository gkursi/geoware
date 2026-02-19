package xyz.qweru.geo.client.event

import net.minecraft.network.PacketListener
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ServerPacketListener
import xyz.qweru.basalt.Cancellable

abstract class PacketEvent<T : PacketListener> : Cancellable() {
    lateinit var packet: Packet<T>
}

object PacketQueueEvent : PacketEvent<ServerPacketListener>()
object PacketSendEvent : PacketEvent<ServerPacketListener>()
object PacketReceiveEvent : PacketEvent<ClientGamePacketListener>()
object PreMoveSendEvent
object PostMoveSendEvent
