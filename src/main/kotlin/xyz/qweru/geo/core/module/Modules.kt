package xyz.qweru.geo.core.module

import com.google.gson.JsonObject
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import xyz.qweru.geo.client.module.combat.ModuleAimAssist
import xyz.qweru.geo.client.module.combat.ModuleHitbox
import xyz.qweru.geo.client.module.combat.ModuleReach
import xyz.qweru.geo.client.module.combat.ModuleTriggerBot
import xyz.qweru.geo.client.module.config.ModuleSwap
import xyz.qweru.geo.client.module.move.ModuleJumpReset
import xyz.qweru.geo.client.module.move.ModuleSafeWalk
import xyz.qweru.geo.client.module.player.ModuleFastUse
import xyz.qweru.geo.client.module.player.ModuleMCA
import xyz.qweru.geo.core.system.System

class Modules() : System("modules") {

    val sorted: ObjectArrayList<Module> = ObjectArrayList()

    override fun initThis() {
        sorted.clear()

        add(ModuleFastUse())
        add(ModuleJumpReset())
        add(ModuleTriggerBot())
        add(ModuleHitbox())
        add(ModuleReach())
        add(ModuleSafeWalk())
        add(ModuleAimAssist())
        add(ModuleSwap())
        add(ModuleMCA())

        sorted.sortWith(Comparator.comparing(Module::name))
    }

    override fun add(system: System) {
        if (system !is Module) throw IllegalArgumentException()
        sorted.add(system)
        super.add(system)
    }

    override fun loadThis(json: JsonObject) {}
    override fun saveThis(json: JsonObject) {}
}