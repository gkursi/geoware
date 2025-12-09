package xyz.qweru.geo.client.helper.anticheat

import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ServerboundClientTickEndPacket
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket
import xyz.qweru.geo.client.helper.network.PacketHelper
import xyz.qweru.geo.client.helper.player.inventory.InvHelper

enum class AntiCheat {
    NONE,
    GRIM {
        override fun finishPacket(packet: Packet<*>) {
            when (packet) {
                is ServerboundMovePlayerPacket -> PacketHelper.sendPacket(ServerboundClientTickEndPacket.INSTANCE, this)
            }
        }
    },
    NCP,
    MATRIX;

    open fun setupPacket(packet: Packet<*>): EvalResult = EvalResult.SUCCESS
    open fun finishPacket(packet: Packet<*>) {}
}