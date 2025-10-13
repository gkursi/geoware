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
            val packet = e.packet as ResourcePackStatusC2SPacket
            if (packet.status == ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED) {
                e.cancelled = true // not the best way, but works
                if (disconnect && inGame) mc.thePlayer.networkHandler.onDisconnected(info)
            }
        }
    }
}