package xyz.qweru.geo.core.manager.rotation

data class RotationConfig(val isSync: Boolean = false, val mouseFix: Boolean = false, val moveFix: Boolean = false) {
    companion object {
        val DEFAULT = RotationConfig()
    }
}
