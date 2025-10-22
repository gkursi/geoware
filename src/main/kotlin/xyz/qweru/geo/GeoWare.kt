package xyz.qweru.geo

import net.fabricmc.api.ModInitializer
import xyz.qweru.geo.core.Global
import xyz.qweru.geo.core.manager.command.CommandManager
import xyz.qweru.geo.core.event.EventBus
import xyz.qweru.geo.core.manager.Managers
import xyz.qweru.geo.core.system.Systems
import xyz.qweru.geo.core.system.config.Configs

class GeoWare : ModInitializer {

    override fun onInitialize() {
        val i = System.nanoTime()

        Systems.init()
        CommandManager.register()
        Managers.init()
        Systems.get(Configs::class).init()

        EventBus.subscribe(this)
        createInputListeners()

        Global.logger.info("Initialized ${Global.MOD} in ${(System.nanoTime() - i)/1000000}ms")
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
}
