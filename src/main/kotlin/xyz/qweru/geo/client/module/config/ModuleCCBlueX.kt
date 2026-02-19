package xyz.qweru.geo.client.module.config

import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket
import xyz.qweru.geo.client.event.PacketReceiveEvent
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.impl.module.Module

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