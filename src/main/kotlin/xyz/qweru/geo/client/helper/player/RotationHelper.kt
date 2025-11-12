package xyz.qweru.geo.client.helper.player

import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import xyz.qweru.geo.client.helper.entity.Target
import xyz.qweru.geo.core.Global.mc
import xyz.qweru.geo.core.manager.rotation.Rotation
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import kotlin.math.sqrt

object RotationHelper {
    fun get(target: Vec3): Rotation {
        val player: Vec3 = mc.thePlayer.eyePosition
        val dx = target.x - player.x
        val dy = target.y - player.y
        val dz = target.z - player.z
        val dist = sqrt(dx * dx + dz * dz)
        val pitch = Mth.wrapDegrees((-(Mth.atan2(dy, dist) * 57.2957763671875)).toFloat())
        val yaw = Mth.wrapDegrees((Mth.atan2(dz, dx) * 57.2957763671875).toFloat() - 90.0f)
        return Rotation(yaw, pitch)
    }

    fun get(target: Entity) = get(optimalPoint(target))

    fun get(target: Target) = get(target.visiblePoint ?: optimalPoint(target.player))

    fun getAngle(target: Entity): Float {
        val current = floatArrayOf(Mth.wrapDegrees(mc.thePlayer.yRot), mc.thePlayer.xRot)
        val target = get(target)
        val dy = Mth.wrapDegrees(target.yaw) - current[0]
        val dp = target.pitch - current[1]
        return sqrt(dy * dy + dp * dp)
    }

    fun getDelta(target: Entity): FloatArray {
        val angles = floatArrayOf(0f, 0f)
        get(target).set(angles)
        angles[0] = Mth.wrapDegrees(angles[0] - mc.thePlayer.yRot)
        angles[1] = angles[1] - mc.thePlayer.xRot
        return angles
    }

    fun gcd(): Float {
        val f = (mc.options.sensitivity().get() * 0.6f + 0.2f).toFloat()
        return f * f * f * 1.2f
    }

    private fun optimalPoint(target: Entity) = target.position().add(0.0, target.bbHeight * (if (target.y < mc.thePlayer.y) 0.45 else 0.65), 0.0)

}