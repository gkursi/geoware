package xyz.qweru.geo.core.game.rotation

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket
import net.minecraft.network.protocol.game.ServerboundUseItemPacket
import net.minecraft.world.phys.HitResult
import xyz.qweru.basalt.EventPriority
import xyz.qweru.geo.client.event.*
import xyz.qweru.geo.client.helper.math.random.LayeredRandom
import xyz.qweru.geo.client.helper.player.RotationHelper
import xyz.qweru.geo.client.helper.player.RotationHelper.circularDistance
import xyz.qweru.geo.client.module.config.ModuleRotation
import xyz.qweru.geo.core.Core.mc
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.helper.manage.ProposalHandler
import xyz.qweru.geo.core.game.rotation.interpolate.HumanInterpolationEngine
import xyz.qweru.geo.core.system.SystemCache
import xyz.qweru.geo.core.ui.notification.Notifications
import xyz.qweru.geo.extend.kotlin.array.copy2
import xyz.qweru.geo.extend.kotlin.array.getRotation
import xyz.qweru.geo.extend.kotlin.array.setRotation
import xyz.qweru.geo.extend.minecraft.game.theLevel
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import xyz.qweru.geo.extend.minecraft.world.hit
import xyz.qweru.geo.mixin.network.packet.ServerboundUseItemPacketAccessor
import xyz.qweru.multirender.api.API
import kotlin.io.path.Path

object RotationHandler : ProposalHandler<Rotation>() {

    val rot: FloatArray = floatArrayOf(0f, 0f)
    val clientRot: FloatArray = floatArrayOf(0f, 0f)
    val initialRot: FloatArray = floatArrayOf(0f, 0f)
    // prevents us from assuming a rotation too early
    val lastSentRot: FloatArray = floatArrayOf(0f, 0f)
    var engine: InterpolationEngine = HumanInterpolationEngine

    val random = LayeredRandom.DEFAULT
    val rotationConfig: ModuleRotation by SystemCache.getModule()
    var crosshairTarget: HitResult? = null
        private set

    private val fixMouse
        get() = rotationConfig.mouseFix || current?.config?.mouseFix == true
    private val fixMovement
        get() = rotationConfig.moveFix || current?.config?.moveFix == true

    @Handler(priority = EventPriority.FIRST)
    private fun preTick(e: PreTickEvent) = handleProposal()

    @Handler(priority = EventPriority.LAST)
    private fun preSendMove(e: PreMoveSendEvent) =
        rot.setRotation(mc.thePlayer)

    @Handler(priority = EventPriority.FIRST)
    private fun postSendMove(e: PostMoveSendEvent) =
        clientRot.setRotation(mc.thePlayer)

    @Handler(priority = EventPriority.LAST)
    private fun preMove(e: PreMovementTickEvent) {
        if (fixMovement) rot.setRotation(mc.thePlayer)
    }

    @Handler(priority = EventPriority.FIRST)
    private fun postMove(e: PostMovementTickEvent) {
        if (fixMovement) clientRot.setRotation(mc.thePlayer)
    }

    @Handler(priority = EventPriority.LAST)
    private fun preCrosshair(e: PreCrosshair) {
        if (mc.player == null) return

        clientRot.getRotation(mc.thePlayer)
        if (current == null) {
            // todo: properly rotate back
            val preYaw = rot[0]
            rot.copy2(clientRot)
            rot[0] = RotationHelper.unwrapYaw(rot[0], preYaw)
        } else if (current?.config?.forceClient == true) {
            clientRot.copy2(rot)
        }

        interpolateRot()
        crosshairTarget = mc.theLevel.hit(mc.thePlayer.entityInteractionRange(), rot)

        if (!fixMouse) return
        rot.setRotation(mc.cameraEntity ?: mc.thePlayer)
    }

    @Handler
    private fun postCrosshair(e: PostCrosshair) {
        if (mc.player == null || !fixMouse) return
        clientRot.setRotation(mc.cameraEntity ?: mc.thePlayer)
    }

    @Handler
    private fun packetSent(e: PacketSendEvent) {
        when (val packet = e.packet) {
            is ServerboundMovePlayerPacket -> {
                lastSentRot[0] = packet.getYRot(lastSentRot[0])
                lastSentRot[1] = packet.getXRot(lastSentRot[1])
            }
            is ServerboundUseItemPacketAccessor -> {
//                packet.geo_setYRot(lastSentRot[0])
//                packet.geo_setXRot(lastSentRot[1])
            }
            is ServerboundUseItemOnPacket -> {
                Notifications.info("UseItemOn with sequence ${packet.sequence}")
            }
        }
    }

    fun mouseRotation(): FloatArray =
        if (fixMouse) rot else clientRot

    override fun propose(proposal: Rotation, priority: Int): Boolean {
        if (!super.propose(proposal, priority)) return false
        initialRot.copy2(rot)
        return true
    }

    fun isLookingAt(rot: Rotation) =
        rot.equals(lastSentRot)

    private fun interpolateRot() {
        val rotation = current ?: return
        val dt = API.base.getDeltaTime()

        var yawDelta = engine.stepYaw(initialRot[0], rotation.yaw, rot[0]) * dt
        var pitchDelta = engine.stepPitch(initialRot[1], rotation.pitch, rot[1]) * dt

        val start = initialRot[0]
        val current = rot[0]
        val end = rotation.yaw

        if (circularDistance(start, current + yawDelta) > circularDistance(start, end)) {
            yawDelta = end - current
        }

        if (rotationConfig.gcdFix) {
            val gcd = RotationHelper.gcd()
            yawDelta -= yawDelta % gcd
            pitchDelta -= pitchDelta % gcd
        }

        rot[0] += yawDelta
        rot[1] += pitchDelta

        engine.onYawDelta(yawDelta)
        engine.onPitchDelta(pitchDelta)

        rotation.applied = true // rotate towards the rotation at least once before allowing its removal
    }
}