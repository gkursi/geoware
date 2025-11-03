package xyz.qweru.geo.client.helper.network

import net.minecraft.entity.Entity
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket
import net.minecraft.util.Hand
import xyz.qweru.geo.core.Global.mc
import xyz.qweru.geo.client.helper.anticheat.AntiCheat
import xyz.qweru.geo.client.helper.version.ViaHelper
import xyz.qweru.geo.extend.thePlayer

object PacketHelper {
    fun sendPacket(packet: Packet<*>, anticheat: AntiCheat = AntiCheat.NONE) {
        anticheat.setupPacket(packet)
        mc.networkHandler!!.sendPacket(packet)
        anticheat.finishPacket(packet)
    }

    fun attackAndSwing(entity: Entity) {
        if (ViaHelper.isReverseHitOrder()) {
            sendPacket(HandSwingC2SPacket(Hand.MAIN_HAND))
            attack(entity)
        } else {
            attack(entity)
            sendPacket(HandSwingC2SPacket(Hand.MAIN_HAND))
        }
    }

    fun attack(entity: Entity) =
        sendPacket(PlayerInteractEntityC2SPacket.attack(entity, mc.thePlayer.isSneaking))
}