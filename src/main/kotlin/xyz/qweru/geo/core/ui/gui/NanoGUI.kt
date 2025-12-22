package xyz.qweru.geo.core.ui.gui

import multirender.nanovg.NanoContext
import multirender.wm.WindowManager
import org.lwjgl.glfw.GLFW
import xyz.qweru.geo.core.Core.mc

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
            GLFW.GLFW_KEY_Q ->
                WindowManager.closeFocused()
        }
    }

    private fun canListen(): Boolean =
        GLFW.glfwGetKey(mc.window.window, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS
            && WindowManager.open
}