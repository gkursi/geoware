package xyz.qweru.geo.core.manager.combat

// TODO: somehow detect sweeps
data class Attack(@Volatile var sprint: Boolean = false, @Volatile var crit: Boolean = false, /*var sweep: Boolean = false*/) {
    fun reset() {
        sprint = false
        crit = false
    }
}
