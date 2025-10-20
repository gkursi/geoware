package xyz.qweru.geo.client.event

import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.listener.PacketListener
import net.minecraft.network.listener.ServerPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.server.network.ServerPlayNetworkHandler
import xyz.qweru.geo.core.event.Cancellable

abstract class PacketEvent<T : PacketListener> : Cancellable() {
    lateinit var packet: Packet<T>
}

object PacketSendEvent : PacketEvent<ServerPlayPacketListener>()
object PacketReceiveEvent : PacketEvent<ClientPlayPacketListener>()
