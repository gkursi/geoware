package xyz.qweru.geo.core.ui

import io.github.humbleui.skija.BackendRenderTarget
import io.github.humbleui.skija.Canvas
import io.github.humbleui.skija.ColorSpace
import io.github.humbleui.skija.ColorType
import io.github.humbleui.skija.DirectContext
import io.github.humbleui.skija.FramebufferFormat
import io.github.humbleui.skija.Paint
import io.github.humbleui.skija.Surface
import io.github.humbleui.skija.SurfaceOrigin
import io.github.humbleui.types.Rect
import org.lwjgl.opengl.GL11
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.multirender.api.API
import xyz.qweru.multirender.api.input.event.KeyPressEvent
import xyz.qweru.multirender.api.render.event.WindowCreateEvent
import xyz.qweru.multirender.api.render.event.WindowSizeChangeEvent
import xyz.qweru.multirender.api.util.color.Color
import xyz.qweru.multirender.gl.GlEventHandler
import xyz.qweru.multirender.gl.GlRenderEvent
import xyz.qweru.multirender.gl.GlState

object UI {
    private var context: DirectContext? = null
    private val state by lazy {
        GlState().also { it.save() }
    }
    private var renderTarget: BackendRenderTarget? = null
    private var surface: Surface? = null
    private lateinit var _canvas: Canvas
    val canvas: Canvas
        get() = _canvas

    fun init() {
        API.events.subscribe(this)
        GlEventHandler.init()
    }

    @Handler
    private fun createWindow(event: WindowCreateEvent) {
        if (context != null) throw IllegalStateException()
        context = DirectContext.makeGL()
        updateTarget(event.width, event.height)
    }

    @Handler
    private fun resizeWindow(event: WindowSizeChangeEvent) =
        updateTarget(event.width, event.height)

    @Handler
    private fun onRender(e: GlRenderEvent) {
        state.restore()
        val rect = Rect.makeXYWH(2f, 2f, 100f, 30f)
            .withRadii(5f)
        val paint = Paint().apply {
            setColor(Color.pink.rgb)
            setAntiAlias(true)
        }
        canvas.drawRectShadow(rect, -5f, -5f, 2f, Color.darkGray.rgb)
        canvas.drawRRect(rect, paint)
        context!!.flush()
        state.save()
    }

    @Handler
    private fun keyPress(e: KeyPressEvent) {

    }

    private fun updateTarget(width: Int, height: Int) {
        renderTarget?.close()
        surface?.close()

        renderTarget = BackendRenderTarget.makeGL(
            width,
            height,  /*samples*/
            0,  /*stencil*/
            8,
            GL11.glGetInteger(0x8CA6),
            FramebufferFormat.GR_GL_RGBA8
        )

        surface = Surface.wrapBackendRenderTarget(
            context!!,
            renderTarget!!,
            SurfaceOrigin.BOTTOM_LEFT,
            ColorType.RGBA_8888,
            ColorSpace.getSRGB()
        )

        _canvas = surface!!.canvas
    }
}