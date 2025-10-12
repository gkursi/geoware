package xyz.qweru.geo.client.module.misc

import net.minecraft.network.DisconnectionInfo
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket
import net.minecraft.text.Text
import xyz.qweru.geo.client.event.PacketSendEvent
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.thePlayer

class ModuleNoPackFingerprint : Module("NoFingerprint", "Prevents the server from fingerprinting the client", Category.MISC) {

    companion object {
        val info: DisconnectionInfo by lazy { DisconnectionInfo(Text.of("Fingerprinting detected")) }
    }

    val sg = settings.group("General")
    var disconnect by sg.boolean("Disconnect", "Disconnect if fingerprinting was detected", false)

    @Handler
    private fun onPacketSend(e: PacketSendEvent) {
        if (e.packet is ResourcePackStatusC2SPacket) {
            val pack = e.packet as ResourcePackStatusC2SPacket
            if (pack.status == ResourcePackStatusC2SPacket.Status.FAILED_DOWNLOAD) {
                e.cancelled = true
                if (disconnect && inGame) mc.thePlayer.networkHandler.onDisconnected(info)
            }
        }
    }
}