package xyz.qweru.geo.client.module.misc

import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket
import xyz.qweru.geo.client.event.PacketSendEvent
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.module.Category
import xyz.qweru.geo.core.module.Module

class ModuleNoPackFingerprint : Module("NoFingerprint", "Prevents the server from fingerprinting the client", Category.MISC) {
    @Handler
    private fun onPacketSend(e: PacketSendEvent) {
        if (e.packet is ResourcePackStatusC2SPacket) {
            val status = e.packet as ResourcePackStatusC2SPacket

        }
    }
}