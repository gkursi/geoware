package xyz.qweru.geo.core.ui.gui

import multirender.nanovg.NanoContext
import multirender.wm.backend.WMBackend
import xyz.qweru.geo.core.Core.mc

object WM : WMBackend {
    private val window by lazy { mc.window }
    internal lateinit var context: NanoContext
    private var originX = 0f
    private var originY = 0f

    override fun moveOriginBy(x: Float, y: Float) {
        context.moveOrigin(x, y)
        originX += x
        originY += y
    }

    override fun restoreOrigin() {
        context.moveOrigin(-originX, -originY)
        originX = 0f
        originY = 0f
    }

    override fun getRemainingWidth(): Float = window.width - originX
    override fun getRemainingHeight(): Float = window.height - originY

    override fun setScissor(x: Float, y: Float, w: Float, h: Float) =
        context.setScissor(x, y, w, h)
    override fun clearScissor() = context.clearScissor()

    /* Todo */

    override fun drawBorder(x: Float, y: Float, w: Float, h: Float, radius: Float) {}

    override fun getMouseX(): Float = 0f
    override fun getMouseY(): Float = 0f
}