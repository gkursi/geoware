package xyz.qweru.geo.core.game.rotation

import xyz.qweru.geo.core.helper.manage.Proposal

data class Rotation(val yaw: Float, val pitch: Float, val config: RotationConfig = RotationConfig.DEFAULT) : Proposal {

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

    var applied = false
        internal set

    override fun isComplete(): Boolean = applied || config.sync

    fun set(array: FloatArray) {
        array[0] = yaw
        array[1] = pitch
    }

    override fun hashCode(): Int {
        var result = pitch.hashCode()
        result = 31 * result + yaw.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rotation

        if (yaw != other.yaw) return false
        if (pitch != other.pitch) return false
        if (config != other.config) return false

        return true
    }

    override fun toString(): String {
        return "yaw=$yaw, pitch=$pitch, config=$config"
    }
}
