package xyz.qweru.geo.helper.player

import net.minecraft.entity.Entity
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import xyz.qweru.geo.core.Glob.mc
import xyz.qweru.geo.extend.thePlayer
import kotlin.math.sqrt

object RotationHelper {
    fun get(target: Vec3d): FloatArray {
        val player: Vec3d = mc.thePlayer.eyePos
        val dx = target.x - player.x
        val dy = target.y - player.y
        val dz = target.z - player.z
        val dist = sqrt(dx * dx + dz * dz)
        val pitch = MathHelper.wrapDegrees((-(MathHelper.atan2(dy, dist) * 57.2957763671875)).toFloat())
        val yaw = MathHelper.wrapDegrees((MathHelper.atan2(dz, dx) * 57.2957763671875).toFloat() - 90.0f)
        return floatArrayOf(yaw, pitch)
    }

    fun get(target: Entity) = get(target.pos.add(0.0, target.height * 0.65, 0.0))

    fun getAngle(target: Entity): Float {
        val current = floatArrayOf(mc.thePlayer.yaw, mc.thePlayer.pitch)
        val target = get(target)
        val dy = target[0] - current[0]
        val dp = target[1] - current[1]
        return sqrt(dy * dy + dp * dp)
    }

    fun getDelta(target: Entity): FloatArray {
        val angles = get(target)
        angles[0] = MathHelper.wrapDegrees(angles[0] - mc.thePlayer.yaw)
        angles[1] = angles[1] - mc.thePlayer.pitch
        return angles
    }

}