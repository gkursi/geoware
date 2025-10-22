package xyz.qweru.geo.client.helper.anticheat

import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.c2s.play.ClientTickEndC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import xyz.qweru.geo.client.helper.network.PacketHelper

enum class AntiCheat {
    NONE,
    GRIM {
        override fun finishPacket(packet: Packet<*>) {
            if (packet is PlayerMoveC2SPacket) {
                PacketHelper.sendPacket(ClientTickEndC2SPacket.INSTANCE, this)
            }
        }
    },
    NCP,
    MATRIX;

    open fun setupPacket(packet: Packet<*>) {}
    open fun finishPacket(packet: Packet<*>) {}
}