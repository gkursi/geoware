package xyz.qweru.geo

import net.fabricmc.api.ModInitializer
import org.jetbrains.skija.Color4f
import org.jetbrains.skija.Paint
import org.jetbrains.skija.PaintMode
import xyz.qweru.geo.client.event.UIRenderEvent
import xyz.qweru.geo.core.Core
import xyz.qweru.geo.core.event.EventBus
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.Systems
import xyz.qweru.geo.core.system.config.Configs

class GeoWare : ModInitializer {

    override fun onInitialize() {
        val i = System.nanoTime()

        Core.init()
        Systems.init()
        Systems.get(Configs::class).init()

        EventBus.subscribe(this)
        createInputListeners()

        Core.logger.info("Initialized ${Core.MOD} in ${(System.nanoTime() - i)/1000000}ms")
    }

    private fun createInputListeners() {
        // FIXME this is called before the api is initialized
//        API.mouseHandler.registerCallback { window, x, y ->
//            if (window == mc.window.handle) {
//                MouseMoveEvent.x = x
//                MouseMoveEvent.y = y
//                Events.post(MouseMoveEvent)
//            }
//        }
//        API.keyboardHandler.registerCallback { window, key, action, mod ->
//            if (window == mc.window.handle) {
//                KeyboardInputEvent.action = action
//                KeyboardInputEvent.button = key
//                Events.post(KeyboardInputEvent)
//            }
//        }
    }

    @Handler
    fun renderUI(e: UIRenderEvent) {
        val canvas = e.canvas
        val paint = Paint()
            .setMode(PaintMode.STROKE_AND_FILL)
            .setAntiAlias(true)
            .setColor4f(Color4f(.67f, .420f, .67f, .9f))

        canvas.drawCircle(2f, 2f, 20f, paint)
        paint.close()
    }
}
