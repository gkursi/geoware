package xyz.qweru.geo.client.event

import net.minecraft.network.packet.Packet
import xyz.qweru.geo.core.event.Cancellable

abstract class PacketEvent : Cancellable() {
    lateinit var packet: Packet<*>
}

object PacketSendEvent : PacketEvent()
object PacketReceiveEvent : PacketEvent()
