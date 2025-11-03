package xyz.qweru.geo.client.helper.player

import net.minecraft.entity.Entity
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import xyz.qweru.geo.client.helper.entity.Target
import xyz.qweru.geo.core.Global.mc
import xyz.qweru.geo.core.manager.rotation.Rotation
import xyz.qweru.geo.extend.thePlayer
import kotlin.math.sqrt

object RotationHelper {
    fun get(target: Vec3d): Rotation {
        val player: Vec3d = mc.thePlayer.eyePos
        val dx = target.x - player.x
        val dy = target.y - player.y
        val dz = target.z - player.z
        val dist = sqrt(dx * dx + dz * dz)
        val pitch = MathHelper.wrapDegrees((-(MathHelper.atan2(dy, dist) * 57.2957763671875)).toFloat())
        val yaw = MathHelper.wrapDegrees((MathHelper.atan2(dz, dx) * 57.2957763671875).toFloat() - 90.0f)
        return Rotation(yaw, pitch)
    }

    fun get(target: Entity) = get(optimalPoint(target))

    fun get(target: Target) = get(target.visiblePoint ?: optimalPoint(target.player))

    fun getAngle(target: Entity): Float {
        val current = floatArrayOf(MathHelper.wrapDegrees(mc.thePlayer.yaw), mc.thePlayer.pitch)
        val target = get(target)
        val dy = MathHelper.wrapDegrees(target.yaw) - current[0]
        val dp = target.pitch - current[1]
        return sqrt(dy * dy + dp * dp)
    }

    fun getDelta(target: Entity): FloatArray {
        val angles = floatArrayOf(0f, 0f)
        get(target).set(angles)
        angles[0] = MathHelper.wrapDegrees(angles[0] - mc.thePlayer.yaw)
        angles[1] = angles[1] - mc.thePlayer.pitch
        return angles
    }

    fun gcd(): Float {
        val f = (mc.options.mouseSensitivity.getValue() * 0.6f + 0.2f).toFloat()
        return f * f * f * 1.2f
    }

    private fun optimalPoint(target: Entity) = target.pos.add(0.0, target.height * (if (target.y < mc.thePlayer.y) 0.45 else 0.65), 0.0)

}