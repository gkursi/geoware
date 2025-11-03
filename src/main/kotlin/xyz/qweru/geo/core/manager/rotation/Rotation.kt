package xyz.qweru.geo.core.manager.rotation

import net.minecraft.util.math.MathHelper
import xyz.qweru.geo.client.event.WorldRenderEvent
import xyz.qweru.geo.core.Global.mc
import xyz.qweru.geo.core.helper.manage.Proposal

data class Rotation(private val wrappedYaw: Float, val pitch: Float, val isSync: Boolean = false) : Proposal {

    val yaw = unwrapYaw(wrappedYaw, mc.player?.yaw ?: 0f)

    var applied = false
        internal set

    constructor(array: FloatArray, isSync: Boolean = false) : this(array[0], array[1], isSync)

    override fun isComplete(): Boolean = applied

    fun set(array: FloatArray) {
        array[0] = yaw
        array[1] = pitch
    }

    private fun unwrapYaw(yaw: Float, current: Float): Float {
        val wrapped = MathHelper.wrapDegrees(yaw)
        val base = MathHelper.wrapDegrees(current)
        val diff = MathHelper.wrapDegrees(wrapped - base)
        return current + diff
    }
}
