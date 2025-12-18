package xyz.qweru.geo.core.ui

import multirender.nanovg.NanoState
import multirender.nanovg.event.NanoRenderEvent
import multirender.nanovg.util.math.Vec2f
import multirender.wm.WindowManager
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.ui.gui.GUI
import xyz.qweru.multirender.api.API
import java.awt.Color

object UI {
    val colorA = Color(253, 82, 152)
    val colorB = Color(167, 159, 210)

    fun init() {
        API.events.subscribe(this)
        NanoState.init()
        GUI.init()
    }

    @Handler
    fun onRender(e: NanoRenderEvent) {
//        GUI.render(e.context)
    }

}