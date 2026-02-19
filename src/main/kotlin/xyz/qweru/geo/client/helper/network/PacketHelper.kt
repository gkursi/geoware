package xyz.qweru.geo.client.helper.network

import net.minecraft.network.PacketListener
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.*
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import xyz.qweru.geo.client.helper.anticheat.AntiCheat
import xyz.qweru.geo.client.helper.inventory.InvHelper
import xyz.qweru.geo.client.helper.version.ViaHelper
import xyz.qweru.geo.core.Core.mc
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import xyz.qweru.geo.imixin.IMultiplayerGameMode

object PacketHelper {
    fun sendPacket(packet: Packet<out ServerPacketListener>, anticheat: AntiCheat = AntiCheat.none) {
        anticheat.setupPacket(packet)
        ClientConnection.sendPacket(packet)
        anticheat.finishPacket(packet)
    }

    inline fun sendSequencedPacket(anticheat: AntiCheat = AntiCheat.none, crossinline block: (Int) -> Packet<ServerGamePacketListener>) {
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
            swing(InteractionHand.MAIN_HAND, silentSwing)
            attack(entity)
        } else {
            attack(entity)
            swing(InteractionHand.MAIN_HAND, silentSwing)
        }
    }

    fun swing(hand: InteractionHand, silentSwing: Boolean = false) {
        if (silentSwing) {
            sendPacket(ServerboundSwingPacket(hand))
        } else {
            mc.thePlayer.swing(hand)
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
        swing(InteractionHand.MAIN_HAND, silentSwing)
    }

    fun interactEntity(entity: Entity, hand: InteractionHand, secondary: Boolean = false) =
        sendPacket(ServerboundInteractPacket.createInteractionPacket(entity, secondary, hand))

    fun interactEntityAndSwing(entity: Entity, hand: InteractionHand, secondary: Boolean = false, silentSwing: Boolean = false) {
        interactEntity(entity, hand, secondary)
        swing(hand, silentSwing)
    }

    fun interactAtEntity(entity: Entity, hand: InteractionHand, pos: Vec3, secondary: Boolean = false) =
        sendPacket(ServerboundInteractPacket.createInteractionPacket(entity, secondary, hand, pos))

    fun interactAtEntityAndSwing(entity: Entity, hand: InteractionHand, pos: Vec3, secondary: Boolean = false, silentSwing: Boolean = false) {
        interactAtEntity(entity, hand, pos, secondary)
        swing(hand, silentSwing)
    }

    // generics stuff
    inline fun <reified T : PacketListener> handlePacket(packet: Packet<T>) =
        packet.handle((mc.connection as Any) as T)
}