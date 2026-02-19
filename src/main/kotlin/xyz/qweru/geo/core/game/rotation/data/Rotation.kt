package xyz.qweru.geo.core.game.rotation.data

import net.minecraft.util.Mth
import xyz.qweru.geo.client.helper.player.RotationHelper
import kotlin.math.abs

data class Rotation(val yaw: Float, val pitch: Float, val config: RotationConfig = RotationConfig.DEFAULT) {

    companion object {
        // priorities
        const val IMPORTANT_INTERACT = 500
        const val INTERACT = 450
        const val UNIMPORTANT_INTERACT = 400

        const val IMPORTANT_ATTACK = 200
        const val ATTACK = 150
        const val UNIMPORTANT_ATTACK = 100

        const val VERY_IMPORTANT = 1000
        const val IMPORTANT = 300
        const val NORMAL = 10
        const val UNIMPORTANT = 0
    }

    fun set(array: FloatArray) {
        array[0] = yaw
        array[1] = pitch
    }

    // ToDo: Mod360 fix
    fun fix(): Rotation {
        val gcd = RotationHelper.gcd

        val yaw = yaw - (yaw % gcd)
        val pitch = (pitch - (pitch % gcd))
            .coerceIn(-90f, 90f)

        return Rotation(yaw, pitch, config)
    }

    fun approxEquals(other: Rotation, deviation: Float = 2f): Boolean {
        val delta = deltaTo(other)
        return abs(delta.yaw) < deviation && abs(delta.pitch) < deviation
    }

    fun deltaTo(other: Rotation): Rotation =
        Rotation(
            RotationHelper.getYawDelta(other.yaw, yaw),
            other.pitch - pitch
        )

    override fun toString(): String {
        return "Rotation{yaw=$yaw, pitch=$pitch, config=$config}"
    }

    operator fun plus(other: Rotation): Rotation {
        return Rotation(
            Mth.wrapDegrees(yaw + other.yaw),
            pitch + other.pitch
        )
    }
}
