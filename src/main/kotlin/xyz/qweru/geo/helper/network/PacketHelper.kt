package xyz.qweru.geo.helper.network

import net.minecraft.network.packet.Packet
import xyz.qweru.geo.core.Glob.mc
import xyz.qweru.geo.helper.anticheat.AntiCheat

object PacketHelper {
    fun sendPacket(packet: Packet<*>, anticheat: AntiCheat = AntiCheat.NONE) {
        anticheat.setupPacket(packet)
        mc.networkHandler!!.sendPacket(packet)
        anticheat.finishPacket(packet)
    }
}