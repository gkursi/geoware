package xyz.qweru.geo.core.game.rotation

data class RotationConfig(val sync: Boolean = false, val mouseFix: Boolean = false, val moveFix: Boolean = false, val forceClient: Boolean = false) {
    companion object {
        val DEFAULT = RotationConfig()
    }

    override fun toString(): String {
        val sb = StringBuilder("Config( ")

        if (sync) sb.append("sync ")
        if (mouseFix) sb.append("gcd ")
        if (moveFix) sb.append("move ")
        if (forceClient) sb.append("forced ")

        return sb.append(")").toString()
    }
}
