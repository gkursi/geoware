package xyz.qweru.geo.client.helper.network

import net.minecraft.network.PacketListener
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ServerPacketListener
import net.minecraft.network.protocol.game.ServerboundInteractPacket
import net.minecraft.network.protocol.game.ServerboundSwingPacket
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import xyz.qweru.geo.abstraction.game.GHand
import xyz.qweru.geo.abstraction.network.GConnection
import xyz.qweru.geo.core.Global.mc
import xyz.qweru.geo.client.helper.anticheat.AntiCheat
import xyz.qweru.geo.client.helper.version.ViaHelper
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import xyz.qweru.geo.imixin.IPacket

object PacketHelper {
    fun sendPacket(packet: Packet<out ServerPacketListener>, anticheat: AntiCheat = AntiCheat.NONE) {
        anticheat.setupPacket(packet)
        GConnection.sendPacket(packet)
        anticheat.finishPacket(packet)
    }

    fun attackAndSwing(entity: Entity) {
        if (ViaHelper.isReverseHitOrder()) {
            swing(GHand.MAIN_HAND)
            attack(entity)
        } else {
            attack(entity)
            sendPacket(ServerboundSwingPacket(InteractionHand.MAIN_HAND))
        }
    }

    fun swing(hand: GHand) {
        sendPacket(ServerboundSwingPacket(hand.delegate))
    }

    fun attack(entity: Entity) =
        sendPacket(ServerboundInteractPacket.createAttackPacket(entity, mc.thePlayer.isCrouching))

    inline fun <reified T : PacketListener> handlePacket(packet: Packet<T>) =
        packet.handle((mc.connection as Any) as T)
}