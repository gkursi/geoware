package xyz.qweru.geo.core.manager.rotation

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.MathHelper
import xyz.qweru.geo.client.event.*
import xyz.qweru.geo.client.helper.math.RangeHelper
import xyz.qweru.geo.client.helper.math.random.LayeredRandom
import xyz.qweru.geo.client.helper.player.RotationHelper
import xyz.qweru.geo.client.module.config.ModuleRotation
import xyz.qweru.geo.core.Global
import xyz.qweru.geo.core.Global.mc
import xyz.qweru.geo.core.event.EventPriority
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.helper.manage.ProposalHandler
import xyz.qweru.geo.core.manager.rotation.interpolate.HumanInterpolationEngine
import xyz.qweru.geo.core.system.Systems
import xyz.qweru.geo.core.system.module.Modules
import xyz.qweru.geo.extend.copy2
import xyz.qweru.geo.extend.getRotation
import xyz.qweru.geo.extend.setRotation
import xyz.qweru.geo.extend.hit
import xyz.qweru.geo.extend.thePlayer
import xyz.qweru.multirender.api.API
import java.lang.Math.clamp
import java.util.Arrays

object RotationHandler : ProposalHandler<Rotation>() {

    val rot: FloatArray = floatArrayOf(0f, 0f)
    val clientRot: FloatArray = floatArrayOf(0f, 0f)
    val initialRot: FloatArray = floatArrayOf(0f, 0f)
    // prevents us from assuming a rotation too early
    val lastSentRot: FloatArray = floatArrayOf(0f, 0f)
    var engine: InterpolationEngine = HumanInterpolationEngine
    var rotatedBack = false

    val random = LayeredRandom.DEFAULT
    var config: ModuleRotation = Systems.get(Modules::class).get(ModuleRotation::class)
    var crosshairTarget: HitResult? = null
        private set

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
        if (config.moveFix) rot.setRotation(mc.thePlayer)
    }

    @Handler(priority = EventPriority.FIRST)
    private fun postMove(e: PostMovementTickEvent) {
        if (config.moveFix) clientRot.setRotation(mc.thePlayer)
    }

    @Handler(priority = EventPriority.LAST)
    private fun preCrosshair(e: PreCrosshair) {
        config = Systems.get(Modules::class).get(ModuleRotation::class)
        if (mc.player == null) return

        clientRot.getRotation(mc.thePlayer)
        if (current == null || current?.isSync ?: false) {
            // propose(Rotation(clientRot), -10)
            rot.copy2(clientRot)
        }
        interpolateRot()
        crosshairTarget = hit(mc.thePlayer.entityInteractionRange, rot)

        if (!config.mouseFix) return
        rot.setRotation(mc.cameraEntity ?: mc.thePlayer)
    }

    @Handler
    private fun postCrosshair(e: PostCrosshair) {
        if (mc.player == null || !config.mouseFix) return
        clientRot.setRotation(mc.cameraEntity ?: mc.thePlayer)
    }

    @Handler
    private fun packetSent(e: PacketSendEvent) {
        val packet = e.packet
        if (packet !is PlayerMoveC2SPacket) return
        lastSentRot[0] = packet.getYaw(lastSentRot[0])
        lastSentRot[1] = packet.getPitch(lastSentRot[1])
    }

    fun mouseRotation(): FloatArray =
        if (config.mouseFix) rot else clientRot

    override fun propose(proposal: Rotation, priority: Int): Boolean {
        if (!super.propose(proposal, priority)) return false
        initialRot.copy2(rot)
        return true
    }

    override fun handleProposal() {
        super.handleProposal()
        if (current != null) return
        current = Rotation(clientRot, isSync = true)
        currentPriority = -10
        Global.logger.warn("Started rotating back")
    }

    fun isLookingAt(rotation: Rotation): Boolean =
        inRange(MathHelper.wrapDegrees(rotation.yaw), RangeHelper.fromPoint(MathHelper.wrapDegrees(lastSentRot[0]), config.diff))
        && inRange(rotation.pitch, RangeHelper.fromPoint(lastSentRot[1], config.diff))

    private fun inRange(value: Float, range: ClosedRange<Float>) =
        value >= range.start && value <= range.endInclusive

    private fun interpolateRot() {
        val rotation = current ?: return
        val init = rot.copyOf()
        rot[0] = interpolate(initialRot[0], rot[0], rotation.yaw)
        rot[1] = interpolate(initialRot[1], rot[1], rotation.pitch)

        if (config.gcdFix) {
            val gcd = RotationHelper.gcd()
            rot[0] = rot[0] - rot[0] % gcd
            rot[1] = rot[1] - rot[1] % gcd
        }

        println("Previous: ${init.joinToString(", ")}, current: ${rot.joinToString(",")}, target: $rotation")

        engine.onYawDelta(rot[0] - init[0])
        engine.onPitchDelta(rot[0] - init[0])
        rotation.applied = true // rotate towards the rotation at least once before allowing its removal
    }

    private fun interpolate(start: Float, current: Float, end: Float): Float =
         clamp(
             current + engine.step(if (config.nonlinear) current else start, end) * API.base.getDeltaTime(),
             if (start > end) end else start, if (start > end) start else end
         )
}