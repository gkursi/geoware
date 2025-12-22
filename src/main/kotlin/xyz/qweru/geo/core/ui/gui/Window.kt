package xyz.qweru.geo.core.ui.gui

import multirender.nanovg.util.math.Vec2f
import multirender.wm.backend.WindowBackend
import java.awt.Color

class Window(i: Int) : WindowBackend {
    private val color = when (i % 4) {
        0 -> Color.red
        1 -> Color.green
        2 -> Color.blue
        3 -> Color.pink
        else -> Color.black
    }

    override fun render() {
        NanoWM.context.shape {
            path { rectangle(Vec2f.TOP_LEFT, Vec2f.BOTTOM_RIGHT) }
            fill { paint = solid(color) }
        }
    }
}