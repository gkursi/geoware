package xyz.qweru.geo.core.ui

import multirender.nanovg.NanoState
import multirender.nanovg.event.NanoRenderEvent
import multirender.nanovg.util.math.Vec2f
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.multirender.api.API
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
//        e.context.shape {
//            path {
//                roundedRectangle(
//                    Vec2f.absolute(2f, 2f),
//                    Vec2f.relative(0.5f, .5f),
//                    5f
//                )
//            }
//            fill { paint = boxGradient(
//                Vec2f.absolute(2f, 2f),
//                Vec2f.relative(0.5f, 0.5f),
//                colorB,
//                colorA,
//                20f,
//                1000f
//            ) }
//        }
    }

}