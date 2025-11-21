package xyz.qweru.geo.abstraction.game

import xyz.qweru.geo.core.Core.mc

object Window {
    val handle: Long
        get() = mc.window.window
}