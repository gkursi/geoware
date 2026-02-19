package xyz.qweru.geo.client.module.specific

import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Pose
import net.minecraft.world.entity.Relative
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Items
import net.minecraft.world.phys.EntityHitResult
import xyz.qweru.basalt.EventPriority
import xyz.qweru.geo.client.helper.player.GameOptions
import xyz.qweru.geo.client.helper.network.ClientConnection
import xyz.qweru.geo.client.event.GameRenderEvent
import xyz.qweru.geo.client.event.PacketReceiveEvent
import xyz.qweru.geo.client.event.PacketSendEvent
import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.client.helper.entity.TargetHelper
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.minecraft.game.theLevel
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import xyz.qweru.geo.extend.minecraft.game.withSneak
import xyz.qweru.geo.extend.minecraft.item.isOf
import xyz.qweru.geo.extend.minecraft.network.isInteract
import xyz.qweru.geo.extend.minecraft.world.hit
import xyz.qweru.geo.mixin.network.packet.PositionMoveRotationAccessor

class ModuleGunColony : Module("GunColony", "guncolony.com utils", Category.SPECIFIC) {
    val sg = settings.general
    val noRecoil by sg.boolean("No Recoil", "No recoil", true)
    val silentScope by sg.boolean("Silent Scope", "Silently scopes while shooting", true)
    val fastShoot by sg.boolean("Fast Shoot", "Removes cooldown from semi-automatic guns", true)
    val trigger by sg.boolean("Trigger", "Shoot when hovering an enemy", true)

    @Handler
    fun packetSend(e: PacketSendEvent) {
        val packet = e.packet

        if (!packet.isInteract || !silentScope || !inGame) return
        ClientConnection.sendPacketImmediately(
            ServerboundPlayerInputPacket(mc.thePlayer.input.keyPresses.withSneak(true))
        )
    }

    lateinit var pose: Pose

    @Handler(priority = EventPriority.FIRST)
    fun preC(e: GameRenderEvent) {
        if (inGame && silentScope) {
            pose = mc.thePlayer.pose
            mc.thePlayer.pose = Pose.CROUCHING
        }
    }

    @Handler
    fun postC(e: GameRenderEvent) {
        if (inGame && silentScope) {
            mc.thePlayer.pose = pose
        }
    }

    @Handler
    fun packetReceive(e: PacketReceiveEvent) {
        val packet = e.packet
        if (packet !is ClientboundPlayerPositionPacket || !noRecoil || !inGame) return

        if (packet.relatives.contains(Relative.ROTATE_DELTA) || packet.relatives.containsAll(Relative.ROTATION)) {
            (packet.change as PositionMoveRotationAccessor).geo_setYaw(0f)
            (packet.change as PositionMoveRotationAccessor).geo_setPitch(0f)
        }

        ClientConnection.sendPacketImmediately(
            ServerboundMovePlayerPacket.Rot(
            mc.thePlayer.yRot, mc.thePlayer.xRot, true, mc.thePlayer.horizontalCollision
        ))
    }

    @Handler
    fun onTick(e: PreTickEvent) {
        if (!inGame) return

        if (fastShoot && GameOptions.useKey)
            mc.gameMode?.useItem(mc.thePlayer, InteractionHand.MAIN_HAND)

        if (trigger) {
            val hit = mc.theLevel.hit(128.0)
            if (hit !is EntityHitResult || hit.entity !is Player) return
            if (!TargetHelper.canTarget(hit.entity as Player)) return
            mc.gameMode?.useItem(mc.thePlayer, InteractionHand.MAIN_HAND)
        }
    }

    private fun isHoldingGun() =
        mc.thePlayer.mainHandItem.isOf(Items.IRON_HOE)
}