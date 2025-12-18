package xyz.qweru.geo.core.ui.gui

import multirender.nanovg.NanoContext
import multirender.wm.WindowManager

object GUI {
    private val backendManager = WM

    fun init() {
        WindowManager.setBackend(backendManager)
        var i = 0
        WindowManager.addWindow(Window(i++))
        WindowManager.addWindow(Window(i++))
        WindowManager.addWindow(Window(i++))
        WindowManager.addWindow(Window(i++))
        WindowManager.addWindow(Window(i))
    }

    fun render(context: NanoContext) {
        WM.context = context
        WindowManager.render()
    }
}