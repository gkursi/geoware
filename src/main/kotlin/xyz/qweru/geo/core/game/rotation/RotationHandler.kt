package xyz.qweru.geo.core.game.rotation

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket
import net.minecraft.network.protocol.game.ServerboundUseItemPacket
import net.minecraft.world.phys.HitResult
import xyz.qweru.basalt.EventPriority
import xyz.qweru.geo.client.event.*
import xyz.qweru.geo.client.helper.math.RangeHelper
import xyz.qweru.geo.client.helper.math.random.LayeredRandom
import xyz.qweru.geo.client.helper.player.RotationHelper
import xyz.qweru.geo.client.module.config.ModuleRotation
import xyz.qweru.geo.core.Core.mc
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.game.rotation.interpolate.HumanInterpolationEngine
import xyz.qweru.geo.core.helper.manage.ProposalHandler
import xyz.qweru.geo.core.system.SystemCache
import xyz.qweru.geo.extend.kotlin.array.copy2
import xyz.qweru.geo.extend.kotlin.array.applyRotation
import xyz.qweru.geo.extend.kotlin.array.copyRotationFrom
import xyz.qweru.geo.extend.kotlin.math.wrapped
import xyz.qweru.geo.extend.minecraft.game.theLevel
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import xyz.qweru.geo.extend.minecraft.world.hit
import xyz.qweru.geo.mixin.network.packet.ServerboundUseItemPacketAccessor
import xyz.qweru.multirender.api.API
import kotlin.math.abs

object RotationHandler : ProposalHandler<Rotation>() {

    val rot: FloatArray = floatArrayOf(0f, 0f)
    val clientRot: FloatArray = floatArrayOf(0f, 0f)
    val initialRot: FloatArray = floatArrayOf(0f, 0f)
    // prevents us from assuming a rotation too early
    val lastSentRot: FloatArray = floatArrayOf(0f, 0f)

    val random = LayeredRandom.DEFAULT
    var engine: InterpolationEngine = HumanInterpolationEngine
    val rotationConfig: ModuleRotation by SystemCache.getModule()
    var crosshairTarget: HitResult? = null
        private set

    private var rotatingBack = false

    private val fixMouse
        get() = rotationConfig.mouseFix || current?.config?.mouseFix == true
    private val fixMovement
        get() = rotationConfig.moveFix || current?.config?.moveFix == true

    override var current: Rotation? = null

    override fun resetProposal() {
        if (mc.player == null) return

        val client = Rotation(
            mc.thePlayer.yRot.wrapped,
            mc.thePlayer.xRot,
            RotationConfig(sync = true)
        )

        val syncNeeded = shouldPreserve() && !isLookingAt(client, 5f)

        super.resetProposal()

        if (shouldPreserve()) {
            return
        }

        if (rotatingBack) {
            propose(client, -10)
            rotatingBack = !isLookingAt(client, 5f)
        } else {
            rotatingBack = syncNeeded
        }
    }

    @Handler(priority = EventPriority.FIRST)
    private fun preTick(e: PreTickEvent) = resetProposal()

    @Handler(priority = EventPriority.LAST)
    private fun preSendMove(e: PreMoveSendEvent) =
        rot.applyRotation(mc.thePlayer)

    @Handler(priority = EventPriority.FIRST)
    private fun postSendMove(e: PostMoveSendEvent) =
        clientRot.applyRotation(mc.thePlayer)

    @Handler(priority = EventPriority.LAST)
    private fun preMove(e: PreMovementTickEvent) {
        if (fixMovement) rot.applyRotation(mc.thePlayer)
    }

    @Handler(priority = EventPriority.FIRST)
    private fun postMove(e: PostMovementTickEvent) {
        if (fixMovement) clientRot.applyRotation(mc.thePlayer)
    }

    @Handler(priority = EventPriority.LAST)
    private fun preCrosshair(e: PreCrosshairEvent) {
        if (mc.player == null) return

        clientRot.copyRotationFrom(mc.thePlayer)
        if (current != null) {
            stepRotation()
        } else {
            rot.copy2(clientRot)
        }
        crosshairTarget = mc.theLevel.hit(mc.thePlayer.entityInteractionRange(), rot)

        if (!fixMouse) return
        rot.applyRotation(mc.thePlayer)
    }

    @Handler
    private fun postCrosshair(e: PostCrosshairEvent) {
        if (mc.player == null || !fixMouse) return
        clientRot.applyRotation(mc.thePlayer)
    }

    @Handler
    private fun packetSent(e: PacketSendEvent) {
        when (val packet = e.packet) {
            is ServerboundMovePlayerPacket -> {
                lastSentRot[0] = packet.getYRot(lastSentRot[0])
                lastSentRot[1] = packet.getXRot(lastSentRot[1])
            }
            is ServerboundUseItemPacket -> {
                packet as ServerboundUseItemPacketAccessor
                packet.geo_setYRot(rot[0])
                packet.geo_setXRot(rot[1])
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

    fun isServerLookingAt(rot: Rotation, maxDeviation: Float = rotationConfig.diff) =
        RangeHelper.ofRotationPoint(lastSentRot[0].wrapped, maxDeviation)
            .contains(rot.yaw.wrapped)
                && RangeHelper.ofRotationPoint(lastSentRot[1], maxDeviation)
            .contains(rot.pitch)

    fun isLookingAt(theRot: Rotation, maxDeviation: Float = rotationConfig.diff) =
        RangeHelper.ofRotationPoint(rot[0].wrapped, maxDeviation)
            .contains(theRot.yaw.wrapped)
                && RangeHelper.ofRotationPoint(rot[1], maxDeviation)
            .contains(theRot.pitch)

    private fun stepRotation() {
        val rotation = current ?: return
        val dt = API.base.getDeltaTime()

        val startYaw = initialRot[0].wrapped
        val endYaw = rotation.yaw.wrapped
        val currentYaw = rot[0].wrapped
        val startPitch = initialRot[1]
        val endPitch = rotation.pitch
        val currentPitch = rot[1]

        var yawDelta = engine.stepYaw(startYaw, endYaw, currentYaw) * dt
        var pitchDelta = engine.stepPitch(startPitch, endPitch, currentPitch) * dt

        if (abs((endYaw - currentYaw).wrapped) < abs(yawDelta)) {
            yawDelta = (endYaw - currentYaw).wrapped
        }

        if (abs(endPitch - currentPitch) < abs(pitchDelta)) {
            pitchDelta = endPitch - currentPitch
        }

        if (rotationConfig.gcdFix) {
            val gcd = RotationHelper.gcd()
            yawDelta -= yawDelta % gcd
            pitchDelta -= pitchDelta % gcd
        }

        rot[0] += yawDelta
        rot[1] += pitchDelta

        rot[1] = rot[1].coerceIn(-90f, 90f)

        engine.onYawDelta(yawDelta)
        engine.onPitchDelta(pitchDelta)

        rotation.applied = true // rotate towards the rotation at least once before allowing its removal
    }

    private fun shouldPreserve() =
        current != null && current?.config?.sync == false
}