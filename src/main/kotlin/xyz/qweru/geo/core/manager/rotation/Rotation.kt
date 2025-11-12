package xyz.qweru.geo.core.manager.rotation

import net.minecraft.util.Mth
import xyz.qweru.geo.client.event.WorldRenderEvent
import xyz.qweru.geo.client.helper.math.RangeHelper
import xyz.qweru.geo.core.Global.mc
import xyz.qweru.geo.core.helper.manage.Proposal
import xyz.qweru.geo.core.manager.rotation.RotationHandler.config
import xyz.qweru.geo.core.manager.rotation.RotationHandler.inRange
import xyz.qweru.geo.core.manager.rotation.RotationHandler.lastSentRot

data class Rotation(private val wrappedYaw: Float, val pitch: Float, val isSync: Boolean = false) : Proposal {

    val yaw = unwrapYaw(wrappedYaw, RotationHandler.rot[0])

    var applied = false
        internal set

    constructor(array: FloatArray, isSync: Boolean = false) : this(array[0], array[1], isSync)

    override fun isComplete(): Boolean = applied || isSync

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
            && inRange(Mth.wrapDegrees(this.yaw), RangeHelper.fromRotationPoint(Mth.wrapDegrees(other.yaw), config.diff))
            && inRange(this.pitch, RangeHelper.fromRotationPoint(other.pitch, config.diff))) {
            return true
        } else if (other is FloatArray && other.size == 2
            && inRange(Mth.wrapDegrees(this.yaw), RangeHelper.fromRotationPoint(Mth.wrapDegrees(other[0]), config.diff))
            && inRange(this.pitch, RangeHelper.fromRotationPoint(other[1], config.diff))) {
            return true
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = wrappedYaw.hashCode()
        result = 31 * result + pitch.hashCode()
        result = 31 * result + isSync.hashCode()
        result = 31 * result + yaw.hashCode()
        result = 31 * result + applied.hashCode()
        return result
    }
}
