package xyz.qweru.geo.core.ui

import multirender.nanovg.NanoState
import multirender.nanovg.event.NanoRenderEvent
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.ui.gui.NanoGUI
import xyz.qweru.multirender.api.API
import xyz.qweru.multirender.api.input.event.KeyPressEvent
import java.awt.Color

object UI {
    val colorA = Color(253, 82, 152)
    val colorB = Color(167, 159, 210)

    fun init() {
        API.events.subscribe(this)
        NanoState.init()
        NanoGUI.init()
    }

    @Handler
    fun onRender(e: NanoRenderEvent) {
        NanoGUI.render(e.context)
    }

    @Handler
    fun keyPress(e: KeyPressEvent) {
        NanoGUI.onKey(e.key)
    }

}