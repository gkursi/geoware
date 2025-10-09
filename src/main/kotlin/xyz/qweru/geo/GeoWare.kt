package xyz.qweru.geo

import net.fabricmc.api.ModInitializer
import xyz.qweru.geo.client.module.combat.ModuleAimAssist
import xyz.qweru.geo.client.module.combat.ModuleAutoTotem
import xyz.qweru.geo.client.module.combat.ModuleReach
import xyz.qweru.geo.client.module.combat.ModuleTriggerBot
import xyz.qweru.geo.client.module.move.ModuleSafeWalk
import xyz.qweru.geo.client.module.player.ModuleFastUse
import xyz.qweru.geo.core.Glob
import xyz.qweru.geo.core.command.Commands
import xyz.qweru.geo.core.event.Events
import xyz.qweru.geo.core.module.Modules
import xyz.qweru.geo.core.system.Systems
import xyz.qweru.geo.helper.player.InvHelper

class GeoWare : ModInitializer {

    override fun onInitialize() {
        val i = System.nanoTime()

        Systems.init()
        Commands.register()

        Events.subscribe(this)
        Events.subscribe(InvHelper)
        createInputListeners()

        Glob.logger.info("Initialized ${Glob.mod} in ${(System.nanoTime() - i)/1000000}ms")
        config()
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

    // temporary
    private fun config() {
        Systems.get(Modules::class).get(ModuleFastUse::class).enabled = true
//        Systems.get(Modules::class).get(ModuleJumpReset::class).enabled = true
        Systems.get(Modules::class).get(ModuleTriggerBot::class).enabled = true
//        Systems.get(Modules::class).get(ModuleHitbox::class).enabled = true
        Systems.get(Modules::class).get(ModuleReach::class).enabled = true
        Systems.get(Modules::class).get(ModuleSafeWalk::class).enabled = true
        Systems.get(Modules::class).get(ModuleAimAssist::class).enabled = true
        Systems.get(Modules::class).get(ModuleAutoTotem::class).enabled = true
    }
}
