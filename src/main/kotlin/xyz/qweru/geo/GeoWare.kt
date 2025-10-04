package xyz.qweru.geo

import net.fabricmc.api.ModInitializer
import xyz.qweru.geo.client.module.combat.ModuleReach
import xyz.qweru.geo.client.module.combat.ModuleTriggerBot
import xyz.qweru.geo.client.module.move.ModuleSafeWalk
import xyz.qweru.geo.client.module.player.ModuleFastUse
import xyz.qweru.geo.core.Glob
import xyz.qweru.geo.core.command.Commands
import xyz.qweru.geo.core.event.Events
import xyz.qweru.geo.core.module.Modules
import xyz.qweru.geo.core.system.Systems

class GeoWare : ModInitializer {

    override fun onInitialize() {
        val i = System.nanoTime()
        Systems.init()
        Events.subscribe(this)
        Commands.register()
        Glob.logger.info("Initialized ${Glob.mod} in ${(System.nanoTime() - i)/1000000}ms")
        Systems.get(Modules::class).get(ModuleFastUse::class).enabled = true
//        Systems.get(Modules::class).get(ModuleJumpReset::class).enabled = true
        Systems.get(Modules::class).get(ModuleTriggerBot::class).enabled = true
//        Systems.get(Modules::class).get(ModuleHitbox::class).enabled = true
        Systems.get(Modules::class).get(ModuleReach::class).enabled = true
        Systems.get(Modules::class).get(ModuleSafeWalk::class).enabled = true
    }
}
