package xyz.qweru.geo.abstraction.game

import xyz.qweru.geo.core.Global.mc

object Window {
    val handle: Long
        get() = mc.window.window
}