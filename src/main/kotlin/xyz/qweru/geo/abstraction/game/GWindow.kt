package xyz.qweru.geo.abstraction.game

import xyz.qweru.geo.core.Global.mc

object GWindow {
    val handle: Long
        get() = mc.window.window
}