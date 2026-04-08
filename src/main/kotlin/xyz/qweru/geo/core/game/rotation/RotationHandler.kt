package xyz.qweru.geo.core.game.rotation

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket
import net.minecraft.network.protocol.game.ServerboundUseItemPacket
import net.minecraft.world.phys.HitResult
import xyz.qweru.basalt.EventPriority
import xyz.qweru.geo.client.event.*
import xyz.qweru.geo.client.module.config.ModuleRotation
import xyz.qweru.geo.core.Core.mc
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.SystemCache
import xyz.qweru.geo.extend.kotlin.array.copyRotationFrom
import xyz.qweru.geo.extend.minecraft.entity.setRotation
import xyz.qweru.geo.extend.minecraft.game.theLevel
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import xyz.qweru.geo.extend.minecraft.world.hit

object RotationHandler {

    val serverRotation: FloatArray = floatArrayOf(0f, 0f)
    val clientRotation: FloatArray = floatArrayOf(0f, 0f)
    val lastSentRot: FloatArray = floatArrayOf(0f, 0f)

    private val rotationConfig: ModuleRotation by SystemCache.getModule()
    var crosshairTarget: HitResult? = null
        private set

    private val fixMouse
        get() = rotationConfig.mouseFix
    private val fixMovement
        get() = rotationConfig.moveFix

    @Handler(priority = EventPriority.LAST)
    private fun preSendMove(e: PreMoveSendEvent) =
        mc.thePlayer.setRotation(serverRotation)

    @Handler(priority = EventPriority.FIRST)
    private fun postSendMove(e: PostMoveSendEvent) =
        mc.thePlayer.setRotation(clientRotation)

    @Handler(priority = EventPriority.LAST)
    private fun preMove(e: PreMovementTickEvent) {
        RotationManager.update()
        if (fixMovement) {
            mc.thePlayer.setRotation(serverRotation)
        }
    }

    @Handler(priority = EventPriority.FIRST)
    private fun postMove(e: PostMovementTickEvent) {
        if (fixMovement) {
            mc.thePlayer.setRotation(clientRotation)
        }
    }

    @Handler(priority = EventPriority.LAST)
    private fun preCrosshair(e: PreCrosshairEvent) {
        if (mc.player == null) return

        clientRotation.copyRotationFrom(mc.thePlayer)

        crosshairTarget = mc.theLevel.hit(
            mc.thePlayer.entityInteractionRange(),
            serverRotation
        )

        if (fixMouse) {
            mc.thePlayer.setRotation(serverRotation)
        }
    }

    @Handler
    private fun postCrosshair(e: PostCrosshairEvent) {
        if (mc.player == null || !fixMouse) return
        mc.thePlayer.setRotation(clientRotation)
    }

    @Handler
    private fun packetSent(e: PacketSendEvent) {
        when (val packet = e.packet) {
            is ServerboundMovePlayerPacket -> {
                lastSentRot[0] = packet.getYRot(lastSentRot[0])
                lastSentRot[1] = packet.getXRot(lastSentRot[1])
            }
            is ServerboundUseItemPacket -> {
                lastSentRot[0] = packet.yRot
                lastSentRot[1] = packet.xRot
            }
        }
    }

    fun mouseRotation(): FloatArray =
        if (fixMouse) {
            serverRotation
        } else {
            clientRotation
        }
}