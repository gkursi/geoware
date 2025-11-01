package xyz.qweru.geo.core.manager.rotation

import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.MathHelper
import xyz.qweru.geo.client.event.*
import xyz.qweru.geo.client.helper.math.random.LayeredRandom
import xyz.qweru.geo.client.helper.player.RotationHelper
import xyz.qweru.geo.client.module.config.ModuleRotation
import xyz.qweru.geo.core.Global.mc
import xyz.qweru.geo.core.event.EventPriority
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.helper.manage.ProposalHandler
import xyz.qweru.geo.core.system.Systems
import xyz.qweru.geo.core.system.module.Modules
import xyz.qweru.geo.extend.copy2
import xyz.qweru.geo.extend.getRotation
import xyz.qweru.geo.extend.setRotation
import xyz.qweru.geo.extend.hit
import xyz.qweru.geo.extend.thePlayer
import xyz.qweru.multirender.api.API
import java.lang.Math.clamp

object RotationHandler : ProposalHandler<Rotation>() {

    val rot: FloatArray = floatArrayOf(0f, 0f)
    val clientRot: FloatArray = floatArrayOf(0f, 0f)
    val initialRot: FloatArray = floatArrayOf(0f, 0f)
    val random = LayeredRandom.DEFAULT
    lateinit var module: ModuleRotation
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
        if (module.moveFix) rot.setRotation(mc.thePlayer)
    }

    @Handler(priority = EventPriority.FIRST)
    private fun postMove(e: PostMovementTickEvent) {
        if (module.moveFix) clientRot.setRotation(mc.thePlayer)
    }

    @Handler
    private fun preCrosshair(e: PreCrosshair) {
        module = Systems.get(Modules::class).get(ModuleRotation::class)
        if (mc.player == null) return

        clientRot.getRotation(mc.thePlayer)
        if (current == null) {
            // propose(Rotation(clientRot), -10)
            rot.copy2(clientRot)
        }
        interpolateRot()
        crosshairTarget = hit(mc.thePlayer.entityInteractionRange, rot)

        if (!module.mouseFix) return
        rot.setRotation(mc.cameraEntity ?: mc.thePlayer)
    }

    @Handler
    private fun postCrosshair(e: PostCrosshair) {
        if (mc.player == null || !module.mouseFix) return
        clientRot.setRotation(mc.cameraEntity ?: mc.thePlayer)
    }

    fun mouseRotation(): FloatArray =
        if (module.mouseFix) rot else clientRot

    override fun propose(proposal: Rotation, priority: Int): Boolean {
        if (!super.propose(proposal, priority)) return false
        initialRot.copy2(rot)
        return true
    }

    private fun interpolateRot() {
        val rotation = current ?: return
        val init = rot[0]
        rot[0] = interpolate(initialRot[0], rot[0], rotation.yaw)
        println("Previous: $init, current: ${rot[0]}, target: ${rotation.yaw}")
        rot[1] = interpolate(initialRot[1], rot[1], rotation.pitch)

        if (module.gcdFix) {
            val gcd = RotationHelper.gcd()
            rot[0] = rot[0] - rot[0] % gcd
            rot[1] = rot[1] - rot[1] % gcd
        }
        rotation.applied = true // rotate towards the rotation at least once before allowing its removal
    }

    private fun interpolate(start: Float, current: Float, end: Float): Float {
        val mod = random.double(0.0, 1.0) * module.speed * API.base.getDeltaTime()
        val min = MathHelper.wrapDegrees(if (module.linear) start else current)
        return clamp(
            current + (MathHelper.wrapDegrees(end) - min) * mod.toFloat(),
            if (start > end) end else start, if (start > end) start else end
        )
    }

}