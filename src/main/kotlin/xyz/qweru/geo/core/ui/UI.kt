package xyz.qweru.geo.core.ui

import multirender.nanovg.NanoState
import multirender.nanovg.event.NanoRenderEvent
import multirender.nanovg.util.math.Vec2f
import net.minecraft.world.phys.Vec2
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.multirender.api.API
import xyz.qweru.multirender.api.render.event.PostRenderEvent
import java.awt.Color

object UI {
    val colorA = Color(253, 82, 152)
    val colorB = Color(167, 159, 210)

    fun init() {
        API.events.subscribe(this)
        NanoState.init()
    }

    @Handler
    fun onRender(e: NanoRenderEvent) {

    }

}