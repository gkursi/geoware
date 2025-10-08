package xyz.qweru.geo.client.event

import net.minecraft.network.packet.Packet

abstract class PacketEvent {
    lateinit var packet: Packet<*>
}

object PacketSendEvent : PacketEvent()
object PacketReceiveEvent : PacketEvent()
