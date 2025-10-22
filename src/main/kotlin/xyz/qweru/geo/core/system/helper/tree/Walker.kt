package xyz.qweru.geo.core.system.helper.tree

import xyz.qweru.geo.core.system.System

internal object Walker {
    var size = 0

    fun walk(system: System) {
        size++
        system.getSubsystems().forEach { walk(it) }
    }
}