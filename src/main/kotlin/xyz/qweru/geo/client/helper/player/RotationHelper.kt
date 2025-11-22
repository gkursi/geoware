package xyz.qweru.geo.client.helper.player

import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import xyz.qweru.geo.client.helper.entity.Target
import xyz.qweru.geo.core.Core.mc
import xyz.qweru.geo.core.game.rotation.Rotation
import xyz.qweru.geo.core.game.rotation.RotationConfig
import xyz.qweru.geo.extend.minecraft.entity.pos
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import kotlin.math.sqrt

object RotationHelper {
    fun get(target: Vec3, config: RotationConfig = RotationConfig.DEFAULT): Rotation {
        val player: Vec3 = mc.thePlayer.eyePosition
        val dx = target.x - player.x
        val dy = target.y - player.y
        val dz = target.z - player.z
        val dist = sqrt(dx * dx + dz * dz)
        val pitch = Mth.wrapDegrees((-(Mth.atan2(dy, dist) * 57.2957763671875)).toFloat())
        val yaw = Mth.wrapDegrees((Mth.atan2(dz, dx) * 57.2957763671875).toFloat() - 90.0f)
        return Rotation(yaw, pitch, config)
    }

    fun get(target: Entity, config: RotationConfig = RotationConfig.DEFAULT, point: TargetPoint = TargetPoint.BODY) =
        get(target.pos.add(0.0, point.value.invoke(target), 0.0), config)

    fun get(target: Target, point: TargetPoint = TargetPoint.BODY, config: RotationConfig = RotationConfig.DEFAULT) =
        get(target.visiblePoint ?: target.player.pos.add(0.0, point.value.invoke(target.player), 0.0), config)

    fun getAngle(target: Entity): Float {
        val current = floatArrayOf(Mth.wrapDegrees(mc.thePlayer.yRot), mc.thePlayer.xRot)
        val target = get(target)
        val dy = Mth.wrapDegrees(target.yaw) - current[0]
        val dp = target.pitch - current[1]
        return sqrt(dy * dy + dp * dp)
    }

    fun getDelta(target: Entity, point: TargetPoint = TargetPoint.BODY): FloatArray {
        val angles = floatArrayOf(0f, 0f)
        get(target, point = point).set(angles)
        angles[0] = Mth.wrapDegrees(angles[0] - mc.thePlayer.yRot)
        angles[1] = angles[1] - mc.thePlayer.xRot
        return angles
    }

    fun gcd(): Float {
        val f = (mc.options.sensitivity().get() * 0.6f + 0.2f).toFloat()
        return f * f * f * 1.2f
    }

    enum class TargetPoint(val value: (Entity) -> Double) {
        HEAD({ it.eyeHeight.toDouble() }),
        BODY({ it.bbHeight * 0.5 }),
        LEGS({ it.bbHeight * 0.1 })
    }

}