package xyz.qweru.geo.core.render.skija

import org.jetbrains.skija.BackendRenderTarget
import org.jetbrains.skija.Canvas
import org.jetbrains.skija.ColorSpace
import org.jetbrains.skija.DirectContext
import org.jetbrains.skija.FramebufferFormat
import org.jetbrains.skija.Surface
import org.jetbrains.skija.SurfaceColorFormat
import org.jetbrains.skija.SurfaceOrigin
import xyz.qweru.geo.client.event.FramebufferSizeChangeEvent
import xyz.qweru.geo.client.event.GameRenderEvent
import xyz.qweru.geo.client.event.HudRenderEvent
import xyz.qweru.geo.client.event.PostInitEvent
import xyz.qweru.geo.client.event.SwapBufferEvent
import xyz.qweru.geo.client.event.UIRenderEvent
import xyz.qweru.geo.client.event.VanillaHudRenderEvent
import xyz.qweru.geo.core.Global
import xyz.qweru.geo.core.Global.mc
import xyz.qweru.geo.core.event.EventBus
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.render.state.States

object SkijaManager {
    private lateinit var context: DirectContext
    private var renderTarget: BackendRenderTarget? = null
    private var surface: Surface? = null
    lateinit var canvas: Canvas
        private set

    const val FRAMEBUFFER = 0 // TODO

    @Handler
    private fun createContext(e: PostInitEvent) {
        val width = mc.window.framebufferWidth
        val height = mc.window.framebufferHeight

        context = DirectContext.makeGL()
        updateTarget(width, height)
    }

    @Handler
    private fun onResize(e: FramebufferSizeChangeEvent) =
        updateTarget(e.width, e.height)

    @Handler
    private fun preSwapBuffers(e: VanillaHudRenderEvent) {
//        Global.logger.info("ui")
        States.push()
        UIRenderEvent.canvas = canvas
        HudRenderEvent.canvas = canvas
//        EventBus.post(UIRenderEvent)
//        if (mc.currentScreen == null)
//            EventBus.post(HudRenderEvent)
        context.flush()
        States.pop()
    }

    private fun updateTarget(width: Int, height: Int) {
        surface?.close()
        renderTarget?.close()

        val fbId = mc.framebuffer
        renderTarget = BackendRenderTarget.makeGL(
            width,
            height,  /*samples*/
            0,  /*stencil*/
            8,
            FRAMEBUFFER,
            FramebufferFormat.GR_GL_RGBA8
        )

        surface = Surface.makeFromBackendRenderTarget(
            context,
            renderTarget!!,
            SurfaceOrigin.BOTTOM_LEFT,
            SurfaceColorFormat.RGBA_8888,
            ColorSpace.getSRGB()
        )

        canvas = surface!!.canvas
    }
}