package xyz.qweru.geo.client.helper.anticheat

import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ServerboundClientTickEndPacket
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket
import xyz.qweru.geo.client.helper.network.PacketHelper

enum class AntiCheat {
    NONE,
    GRIM {
        override fun finishPacket(packet: Packet<*>) {
            if (packet is ServerboundMovePlayerPacket) {
                PacketHelper.sendPacket(ServerboundClientTickEndPacket.INSTANCE, this)
            }
        }
    },
    NCP,
    MATRIX;

    open fun setupPacket(packet: Packet<*>) {}
    open fun finishPacket(packet: Packet<*>) {}
}