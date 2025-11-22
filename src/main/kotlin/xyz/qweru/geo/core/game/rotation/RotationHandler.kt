package xyz.qweru.geo.core.game.rotation

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket
import net.minecraft.world.phys.HitResult
import xyz.qweru.geo.client.event.*
import xyz.qweru.geo.client.helper.math.random.LayeredRandom
import xyz.qweru.geo.client.helper.player.RotationHelper
import xyz.qweru.geo.client.module.config.ModuleRotation
import xyz.qweru.geo.core.Core.mc
import xyz.qweru.geo.core.event.EventPriority
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.helper.manage.ProposalHandler
import xyz.qweru.geo.core.game.rotation.interpolate.HumanInterpolationEngine
import xyz.qweru.geo.core.system.SystemCache
import xyz.qweru.geo.extend.kotlin.array.copy2
import xyz.qweru.geo.extend.kotlin.array.getRotation
import xyz.qweru.geo.extend.kotlin.array.setRotation
import xyz.qweru.geo.extend.minecraft.game.theLevel
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import xyz.qweru.geo.extend.minecraft.world.hit
import xyz.qweru.multirender.api.API
import java.lang.Math.clamp

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
            // propose(Rotation(clientRot), -10)
            rot.copy2(clientRot)
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
        val packet = e.packet
        if (packet !is ServerboundMovePlayerPacket) return
        lastSentRot[0] = packet.getYRot(lastSentRot[0])
        lastSentRot[1] = packet.getXRot(lastSentRot[1])
    }

    fun mouseRotation(): FloatArray =
        if (fixMouse) rot else clientRot

    override fun propose(proposal: Rotation, priority: Int): Boolean {
        if (!super.propose(proposal, priority)) return false
        initialRot.copy2(rot)
        return true
    }

    override fun handleProposal() {
        super.handleProposal()
//        val client = Rotation(clientRot, isSync = true)
//        if (current != null || client.equals(lastSentRot) || client.equals(rot)) return
//        current = client
//        currentPriority = -10
//        Global.logger.warn("Started rotating back")
    }

    fun isLookingAt(rot: Rotation) =
        rot.equals(lastSentRot)

    internal fun inRange(value: Float, range: ClosedRange<Float>) =
        value >= range.start && value <= range.endInclusive

    private fun interpolateRot() {
        val rotation = current ?: return
        val init = rot.copyOf()
        rot[0] = interpolate(initialRot[0], rot[0], rotation.yaw)
        rot[1] = interpolate(initialRot[1], rot[1], rotation.pitch)

        if (rotationConfig.gcdFix) {
            val gcd = RotationHelper.gcd()
            rot[0] = rot[0] - rot[0] % gcd
            rot[1] = rot[1] - rot[1] % gcd
        }

        engine.onYawDelta(rot[0] - init[0])
        engine.onPitchDelta(rot[1] - init[1])
        rotation.applied = true // rotate towards the rotation at least once before allowing its removal
    }

    private fun interpolate(start: Float, current: Float, end: Float): Float =
         clamp(
             current + engine.step(if (rotationConfig.nonlinear) current else start, end) * API.base.getDeltaTime(),
             if (start > end) end else start, if (start > end) start else end
         )
}