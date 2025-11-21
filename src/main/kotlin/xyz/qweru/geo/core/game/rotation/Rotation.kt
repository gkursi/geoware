package xyz.qweru.geo.core.game.rotation

import net.minecraft.util.Mth
import xyz.qweru.geo.client.helper.math.RangeHelper
import xyz.qweru.geo.core.helper.manage.Proposal
import xyz.qweru.geo.core.game.rotation.RotationHandler.inRange
import xyz.qweru.geo.core.game.rotation.RotationHandler.rotationConfig

data class Rotation(private val wrappedYaw: Float, val pitch: Float, val config: RotationConfig = RotationConfig.DEFAULT) : Proposal {

    companion object {
        // priorities
        const val IMPORTANT_BLOCK = 500
        const val BLOCK = 450
        const val NOT_IMPORTANT_BLOCK = 400

        const val IMPORTANT_ATTACK = 200
        const val ATTACK = 150
        const val NOT_IMPORTANT_ATTACK = 100

        const val VERY_IMPORTANT = 1000
        const val IMPORTANT = 300
        const val NORMAL = 10
        const val NOT_IMPORTANT = 0
    }

    val yaw = unwrapYaw(wrappedYaw, RotationHandler.rot[0])

    var applied = false
        internal set

    override fun isComplete(): Boolean = applied || config.isSync

    fun set(array: FloatArray) {
        array[0] = yaw
        array[1] = pitch
    }

    private fun unwrapYaw(yaw: Float, current: Float): Float {
        val wrapped = Mth.wrapDegrees(yaw)
        val base = Mth.wrapDegrees(current)
        val diff = Mth.wrapDegrees(wrapped - base)
        return current + diff
    }

    override fun equals(other: Any?): Boolean {
        if (other is Rotation
            && inRange(Mth.wrapDegrees(this.yaw), RangeHelper.fromRotationPoint(Mth.wrapDegrees(other.yaw), rotationConfig.diff))
            && inRange(this.pitch, RangeHelper.fromRotationPoint(other.pitch, rotationConfig.diff))) {
            return true
        } else if (other is FloatArray && other.size == 2
            && inRange(Mth.wrapDegrees(this.yaw), RangeHelper.fromRotationPoint(Mth.wrapDegrees(other[0]), rotationConfig.diff))
            && inRange(this.pitch, RangeHelper.fromRotationPoint(other[1], rotationConfig.diff))) {
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
