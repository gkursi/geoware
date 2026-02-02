package xyz.qweru.geo.client.helper.player

import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import xyz.qweru.geo.client.helper.entity.Target
import xyz.qweru.geo.core.Core.mc
import xyz.qweru.geo.core.game.rotation.Rotation
import xyz.qweru.geo.core.game.rotation.RotationConfig
import xyz.qweru.geo.extend.kotlin.math.wrapped
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
        get(target.pos.add(point.value.invoke(target)), config)

    fun get(target: Target, point: TargetPoint = TargetPoint.BODY, config: RotationConfig = RotationConfig.DEFAULT) =
        get(target.visiblePoint ?: target.player.pos.add(point.value.invoke(target.player)), config)

    fun getAngle(target: Entity): Float {
        val target = get(target)
        return getAngle(target.yaw, target.pitch)
    }

    fun getAngle(vec3: Vec3): Float {
        val target = get(vec3)
        return getAngle(target.yaw, target.pitch)
    }

    fun getAngle(yaw: Float, pitch: Float): Float {
        val dy = Mth.wrapDegrees(yaw) - Mth.wrapDegrees(mc.thePlayer.yRot)
        val dp = pitch - mc.thePlayer.xRot
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

    fun inputToYaw(facing: Float = mc.thePlayer.yRot): Float {
        val forwards = GameOptions.forwards
        val backwards = GameOptions.backwards
        val left = GameOptions.left
        val right = GameOptions.right

        var actualYaw = facing
        var forward = 1f

        if (backwards) {
            actualYaw += 180f
            forward = -0.5f
        } else if (forwards) {
            forward = 0.5f
        }

        if (left) {
            actualYaw -= 90f * forward
        }
        if (right) {
            actualYaw += 90f * forward
        }

        return actualYaw
    }

    fun unwrapYaw(yaw: Float, current: Float): Float =
        current + (yaw.wrapped - current.wrapped).wrapped

    /**
     * converts yaw from [-180; 180] to [0; 360]
     */
    fun toCircular(start: Float, x: Float): Float {
        var d = (x - start).wrapped
        if (d < 0) d += 360f
        return d
    }

    enum class TargetPoint(val value: (Entity) -> Vec3) {
        HEAD({ Vec3(0.0, it.eyeHeight.toDouble(), 0.0) }),
        BODY({ Vec3(0.0, it.bbHeight * 0.5, 0.0) }),
        LEGS({ Vec3(0.0, it.bbHeight * 0.1, 0.0) }),
        NEAR({
            val dimensions = it.getDimensions(it.pose)
            var bestAngle = Float.MAX_VALUE
            var point = Vec3(0.0, 1.0, 0.0)
            
            for (funX in pointsH) {
                val x = funX.invoke(dimensions.width).toDouble()
                for (funY in pointsV) {
                    val y = funY.invoke(dimensions.height).toDouble()
                    for (funZ in pointsH) {
                        val z = funZ.invoke(dimensions.width).toDouble()

                        val vec = Vec3(x, y, z)
                        val angle = getAngle(it.pos.add(vec))
                        if (angle < bestAngle) {
                            bestAngle = angle
                            point = vec
                        }
                    }
                }
            }

            point
        });

        companion object {
            private val pointsH = arrayOf<(Float) -> Float>(
                {w -> 0.1f * w},
                {w -> 0.25f * w},
                {w -> 0.5f * w},
            )

            private val pointsV = arrayOf<(Float) -> Float>(
                {h -> 0.5f * h},
                {h -> 0.75f * h},
                {h -> 0.9f * h},
            )
        }
    }

}