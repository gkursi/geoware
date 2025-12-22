package xyz.qweru.geo.client.helper.anticheat

import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ServerboundClientTickEndPacket
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket
import xyz.qweru.geo.client.helper.network.PacketHelper

abstract class AntiCheat {
    companion object {
        val none = object : AntiCheat() {}
        val grim = object : AntiCheat() {
            override fun finishPacket(packet: Packet<*>) {
                when (packet) {
                    is ServerboundMovePlayerPacket ->
                        PacketHelper.sendPacket(
                            ServerboundClientTickEndPacket.INSTANCE, this
                        )
                }
            }
        }
    }

    open fun setupPacket(packet: Packet<*>) {}
    open fun finishPacket(packet: Packet<*>) {}
}