package xyz.qweru.geo.extend.minecraft.network

import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ServerPacketListener
import net.minecraft.network.protocol.game.ServerboundInteractPacket
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket
import net.minecraft.network.protocol.game.ServerboundUseItemPacket

val Packet<ServerPacketListener>.isInteract: Boolean
    get() = this is ServerboundUseItemPacket || this is ServerboundUseItemOnPacket || this is ServerboundInteractPacket