package xyz.qweru.geo.core.system.module

import com.google.gson.JsonObject
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import xyz.qweru.geo.client.module.combat.ModuleAimAssist
import xyz.qweru.geo.client.module.combat.ModuleAnchorMacro
import xyz.qweru.geo.client.module.combat.ModuleAutoTotem
import xyz.qweru.geo.client.module.combat.ModuleBacktrack
import xyz.qweru.geo.client.module.combat.ModuleHitbox
import xyz.qweru.geo.client.module.combat.ModuleReach
import xyz.qweru.geo.client.module.combat.ModuleTriggerBot
import xyz.qweru.geo.client.module.config.ModuleSwap
import xyz.qweru.geo.client.module.misc.ModuleNoPackFingerprint
import xyz.qweru.geo.client.module.move.ModuleGrimMovement
import xyz.qweru.geo.client.module.move.ModuleVelocity
import xyz.qweru.geo.client.module.move.ModuleSafeWalk
import xyz.qweru.geo.client.module.move.ModuleSprint
import xyz.qweru.geo.client.module.move.ModuleTargetStrafe
import xyz.qweru.geo.client.module.player.ModuleFastUse
import xyz.qweru.geo.client.module.player.ModuleKeyAction
import xyz.qweru.geo.client.module.visual.ModuleViewModel
import xyz.qweru.geo.core.system.System

class Modules() : System("modules", Type.ROOT) {

    val sorted: ObjectArrayList<Module> = ObjectArrayList()

    override fun initThis() {
        sorted.clear()

        add(ModuleFastUse())
        add(ModuleVelocity())
        add(ModuleTriggerBot())
        add(ModuleHitbox())
        add(ModuleReach())
        add(ModuleSafeWalk())
        add(ModuleAimAssist())
        add(ModuleSwap())
        add(ModuleKeyAction())
        add(ModuleNoPackFingerprint())
        add(ModuleAnchorMacro())
        add(ModuleAutoTotem())
        add(ModuleViewModel())
        add(ModuleSprint())
        add(ModuleTargetStrafe())
        add(ModuleGrimMovement())
        add(ModuleBacktrack())
        
        sorted.sortWith(Comparator.comparing(Module::name))
    }

    fun get(name: String): Module? = sorted.find { it.name == name }

    override fun add(system: System) {
        if (system !is Module) throw IllegalArgumentException()
        sorted.add(system)
        super.add(system)
    }

    override fun loadThis(json: JsonObject) {}
    override fun saveThis(json: JsonObject) {

    }
}