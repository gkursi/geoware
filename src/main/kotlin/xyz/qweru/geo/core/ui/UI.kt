package xyz.qweru.geo.core.ui

import com.mojang.blaze3d.opengl.GlTexture
import com.mojang.blaze3d.pipeline.RenderTarget
import io.github.humbleui.skija.*
import io.github.humbleui.types.Rect
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.multirender.api.API
import xyz.qweru.multirender.api.render.event.WindowCreateEvent
import xyz.qweru.multirender.api.render.event.WindowSizeChangeEvent
import xyz.qweru.multirender.api.util.color.Color
import xyz.qweru.multirender.gl.GlEventHandler
import xyz.qweru.multirender.gl.GlRenderEvent

object UI {
    private var context: DirectContext? = null
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
        context!!.resetGLAll()

        val rect = Rect.makeXYWH(2f, 2f, 100f, 30f)
            .withRadii(5f)
        val paint = Paint().apply {
            setColor(Color.pink.rgb)
            setAntiAlias(true)
        }

        canvas.drawRectShadow(rect, -5f, -5f, 2f, Color.darkGray.rgb)
        canvas.drawRRect(rect, paint)

        context!!.flush()
    }

    private fun updateTarget(width: Int, height: Int) {
        surface?.close()
        renderTarget?.close()

        renderTarget = BackendRenderTarget.makeGL(
            width,
            height,  /*samples*/
            0,  /*stencil*/
            8,
            0,
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

private val RenderTarget.colorGlTexture
    get() = this.colorTexture as GlTexture