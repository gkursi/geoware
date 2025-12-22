package xyz.qweru.geo.core.game.rotation

import xyz.qweru.geo.client.helper.math.RangeHelper
import xyz.qweru.geo.client.helper.player.RotationHelper.unwrapYaw
import xyz.qweru.geo.core.helper.manage.Proposal
import xyz.qweru.geo.core.game.rotation.RotationHandler.rotationConfig
import xyz.qweru.geo.extend.kotlin.math.wrapped

data class Rotation(private val wrappedYaw: Float, val pitch: Float, val config: RotationConfig = RotationConfig.DEFAULT) : Proposal {

    companion object {
        // priorities
        const val IMPORTANT_BLOCK = 500
        const val BLOCK = 450
        const val UNIMPORTANT_BLOCK = 400

        const val IMPORTANT_ATTACK = 200
        const val ATTACK = 150
        const val UNIMPORTANT_ATTACK = 100

        const val VERY_IMPORTANT = 1000
        const val IMPORTANT = 300
        const val NORMAL = 10
        const val UNIMPORTANT = 0
    }

    val yaw = unwrapYaw(wrappedYaw, RotationHandler.rot[0])

    var applied = false
        internal set

    override fun isComplete(): Boolean = applied || config.sync

    fun set(array: FloatArray) {
        array[0] = yaw
        array[1] = pitch
    }

    override fun equals(other: Any?): Boolean {
        if (other is Rotation
            && RangeHelper.ofRotationPoint(other.yaw.wrapped, rotationConfig.diff)
                .contains(this.yaw.wrapped)
            && RangeHelper.ofRotationPoint(other.pitch, rotationConfig.diff)
                .contains(this.pitch)) {
            return true
        } else if (other is FloatArray && other.size == 2
            && RangeHelper.ofRotationPoint(other[0].wrapped, rotationConfig.diff)
                .contains(this.yaw.wrapped)
            && RangeHelper.ofRotationPoint(other[1], rotationConfig.diff)
                .contains(this.pitch)) {
            return true
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = pitch.hashCode()
        result = 31 * result + yaw.hashCode()
        return result
    }
}
