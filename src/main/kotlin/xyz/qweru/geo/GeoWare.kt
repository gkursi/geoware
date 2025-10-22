package xyz.qweru.geo

import net.fabricmc.api.ModInitializer
import xyz.qweru.geo.client.module.combat.ModuleAimAssist
import xyz.qweru.geo.client.module.combat.ModuleAnchorMacro
import xyz.qweru.geo.client.module.combat.ModuleAutoTotem
import xyz.qweru.geo.client.module.combat.ModuleReach
import xyz.qweru.geo.client.module.combat.ModuleTriggerBot
import xyz.qweru.geo.client.module.move.ModuleSafeWalk
import xyz.qweru.geo.client.module.move.ModuleSprint
import xyz.qweru.geo.client.module.move.ModuleTargetStrafe
import xyz.qweru.geo.client.module.move.ModuleVelocity
import xyz.qweru.geo.client.module.player.ModuleFastUse
import xyz.qweru.geo.client.module.player.ModuleMCA
import xyz.qweru.geo.client.module.visual.ModuleViewModel
import xyz.qweru.geo.core.Global
import xyz.qweru.geo.core.manager.command.CommandManager
import xyz.qweru.geo.core.event.EventBus
import xyz.qweru.geo.core.manager.Managers
import xyz.qweru.geo.core.system.module.Modules
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
