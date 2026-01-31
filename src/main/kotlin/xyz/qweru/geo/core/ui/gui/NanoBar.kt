package xyz.qweru.geo.core.ui.gui

import multirender.nanovg.util.math.Vec2f
import multirender.wm.bar.Bar
import multirender.wm.manager.Context
import multirender.wm.util.Alignment
import java.awt.Color

object NanoBar : Bar {
    override fun render(context: Context) {
        val width = context.config.barWidth
        when (context.config.barAlign) {
            Alignment.TOP -> {
                draw(NanoWM.getRemainingWidth(), width)
                NanoWM.moveOriginBy(0f, width)
            }
            Alignment.LEFT -> {
                draw(width, NanoWM.getRemainingHeight())
                NanoWM.moveOriginBy(width, 0f)
            }
        }
    }
    
    private fun draw(w: Float, h: Float) {
        NanoWM.context.shape {
            path { rectangle(Vec2f.TOP_LEFT, Vec2f.absolute(w, h)) }
            fill { paint = solid(Color.darkGray) }
        }
    }
}