package xyz.qweru.geo.core.manager.rotation

import net.minecraft.util.math.MathHelper
import xyz.qweru.geo.core.Global.mc
import xyz.qweru.geo.core.helper.manage.Proposal

data class Rotation(private val y: Float, val pitch: Float) : Proposal {

    val yaw = unwrapYaw(y, mc.player?.yaw ?: 0f)

    var applied = false
        internal set

    constructor(array: FloatArray) : this(array[0], array[1])

    override fun isComplete(): Boolean = applied

    fun set(array: FloatArray) {
        array[0] = yaw
        array[1] = pitch
    }

    private fun unwrapYaw(wrapped: Float, current: Float): Float {
        val base = MathHelper.wrapDegrees(current)
        val diff = MathHelper.wrapDegrees(wrapped - base)
        return current + diff
    }
}
