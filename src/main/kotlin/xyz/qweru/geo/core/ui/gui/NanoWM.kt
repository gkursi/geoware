package xyz.qweru.geo.core.ui.gui

import multirender.nanovg.NanoContext
import multirender.wm.WindowManager
import multirender.wm.backend.WMBackend
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import xyz.qweru.geo.core.Core.mc

object NanoWM : Screen(Component.literal("NanoWM")), WMBackend {
    private val window by lazy { mc.window }
    internal lateinit var context: NanoContext
    private var originX = 0f
    private var originY = 0f
    private var screen: Screen? = null

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

    override fun globalAlpha(alpha: Float) =
        context.transformColor(a = alpha)

    override fun open() {
        screen = mc.screen
        mc.setScreen(this)
    }

    override fun close() {
        mc.setScreen(screen)
    }

    override fun onClose() {
        WindowManager.open = false
    }

    /* Todo */

    override fun getMouseX(): Float = 0f
    override fun getMouseY(): Float = 0f
}