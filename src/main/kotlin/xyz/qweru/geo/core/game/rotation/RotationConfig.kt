package xyz.qweru.geo.core.game.rotation

data class RotationConfig(val sync: Boolean = false, val mouseFix: Boolean = false, val moveFix: Boolean = false, val forceClient: Boolean = false) {
    companion object {
        val DEFAULT = RotationConfig()
    }
}
