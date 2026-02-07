package xyz.qweru.geo.client.module.config

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.impl.networking.RegistrationPayload
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import xyz.qweru.geo.client.event.PacketReceiveEvent
import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.client.helper.network.PacketHelper
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.tracking.bot.BotType
import xyz.qweru.geo.core.system.module.Module

class ModuleCCBlueX : Module("CCBlueX", "guh") {

    @Handler
    private fun onPacket(e: PacketReceiveEvent) {
        val packet = e.packet
        if (packet !is ClientboundCustomPayloadPacket) return
        logger.warn("custom payload ${packet.type().id}: ${packet.payload()}")
//        PacketHelper.sendPacket(
//            ServerboundCustomPayloadPacket(
//            )
//        )
    }

}