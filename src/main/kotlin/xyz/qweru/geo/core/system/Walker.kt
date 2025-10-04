package xyz.qweru.geo.core.system

internal object Walker {
    var size = 0

    fun walk(system: System) {
        size++
        system.getSubsystems().forEach { walk(it) }
    }
}