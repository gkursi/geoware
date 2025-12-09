package xyz.qweru.geo.client.helper.network

import net.minecraft.network.PacketListener
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ServerGamePacketListener
import net.minecraft.network.protocol.game.ServerPacketListener
import net.minecraft.network.protocol.game.ServerboundInteractPacket
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket
import net.minecraft.network.protocol.game.ServerboundSwingPacket
import net.minecraft.network.protocol.game.ServerboundUseItemPacket
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import xyz.qweru.geo.abstraction.game.Hand
import xyz.qweru.geo.abstraction.network.ClientConnection
import xyz.qweru.geo.core.Core.mc
import xyz.qweru.geo.client.helper.anticheat.AntiCheat
import xyz.qweru.geo.client.helper.player.inventory.InvHelper
import xyz.qweru.geo.client.helper.version.ViaHelper
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import xyz.qweru.geo.imixin.IMultiplayerGameMode

object PacketHelper {
    fun sendPacket(packet: Packet<out ServerPacketListener>, anticheat: AntiCheat = AntiCheat.NONE): Boolean {
        val result = anticheat.setupPacket(packet)
        if (!result.pass) return false
        ClientConnection.sendPacket(packet)
        anticheat.finishPacket(packet)
        return true
    }

    inline fun sendSequencedPacket(anticheat: AntiCheat = AntiCheat.NONE, crossinline block: (Int) -> Packet<ServerGamePacketListener>) {
        lateinit var packet: Packet<ServerGamePacketListener>
        (mc.gameMode as IMultiplayerGameMode).geo_sequencedPacket {
            packet = block.invoke(it)
            anticheat.setupPacket(packet)
            packet
        }
        anticheat.finishPacket(packet)
    }

    fun attackAndSwing(entity: Entity, silentSwing: Boolean = false) {
        if (ViaHelper.isReverseHitOrder()) {
            swing(Hand.MAIN_HAND, silentSwing)
            attack(entity)
        } else {
            attack(entity)
            swing(Hand.MAIN_HAND, silentSwing)
        }
    }

    fun swing(hand: Hand, silentSwing: Boolean = false) {
        if (silentSwing) {
            sendPacket(ServerboundSwingPacket(hand.delegate))
        } else {
            mc.thePlayer.swing(hand.delegate)
        }
    }

    fun attack(entity: Entity) =
        sendPacket(ServerboundInteractPacket.createAttackPacket(entity, mc.thePlayer.isCrouching))

    fun swap(slot: Int) {
        if (slot == InvHelper.serverSlot) return // prevent BadPacketsA flag
        sendPacket(ServerboundSetCarriedItemPacket(slot))
    }

    fun moveBy(x: Double = 0.0, y: Double = 0.0, z: Double = 0.0) =
        sendPacket(ServerboundMovePlayerPacket.Pos(
            Vec3(mc.thePlayer.x + x, mc.thePlayer.y + y, mc.thePlayer.z + z),
            mc.thePlayer.onGround(), mc.thePlayer.horizontalCollision)
        )

    fun useItem(hand: InteractionHand, yaw: Float = mc.thePlayer.yRot, pitch: Float = mc.thePlayer.xRot) =
        sendSequencedPacket { ServerboundUseItemPacket(hand, it, yaw, pitch) }

    fun useItemAndSwing(hand: InteractionHand, yaw: Float = mc.thePlayer.yRot, pitch: Float = mc.thePlayer.xRot, silentSwing: Boolean = false) {
        useItem(hand, yaw, pitch)
        swing(Hand.MAIN_HAND, silentSwing)
    }

    // generics stuff
    inline fun <reified T : PacketListener> handlePacket(packet: Packet<T>) =
        packet.handle((mc.connection as Any) as T)
}