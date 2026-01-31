package xyz.qweru.geo.core.ui.gui

import multirender.nanovg.NanoContext
import multirender.nanovg.util.math.Vec2f
import multirender.wm.manager.WindowManager
import org.lwjgl.glfw.GLFW
import xyz.qweru.geo.core.Core.mc
import xyz.qweru.multirender.api.API
import kotlin.io.path.Path

object NanoGUI {
    var i = 0

    fun init() {
        WindowManager.setBackend(NanoWM)
        WindowManager.setBar(NanoBar)
    }

    fun render(context: NanoContext) {
        NanoWM.context = context
        WindowManager.render()
    }

    fun onKey(key: Int) {
        if (key == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            WindowManager.open =! WindowManager.open
        }

        if (!canListen()) return

        when (key) {
            GLFW.GLFW_KEY_ENTER ->
                WindowManager.addWindow(Window(i++))
            GLFW.GLFW_KEY_SPACE -> {
                val wh = Vec2f.relative(0.25f, 0.25f)
                WindowManager.addFloatingWindow(
                    Window(i++),
                    API.mouseHandler.x,
                    API.mouseHandler.y,
                    wh.x(), wh.y()
                )
            }
            GLFW.GLFW_KEY_Q ->
                WindowManager.closeFocused()
        }
    }

    private fun canListen(): Boolean =
        GLFW.glfwGetKey(mc.window.window, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS
            && WindowManager.open
}