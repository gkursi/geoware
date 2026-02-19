package xyz.qweru.geo.core.system.module

import com.google.gson.JsonObject
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import xyz.qweru.geo.client.module.combat.*
import xyz.qweru.geo.client.module.config.ModuleCCBlueX
import xyz.qweru.geo.client.module.config.ModulePacket
import xyz.qweru.geo.client.module.config.ModuleRotation
import xyz.qweru.geo.client.module.config.ModuleSwap
import xyz.qweru.geo.client.module.config.ModuleTarget
import xyz.qweru.geo.client.module.misc.ModulePacketLogger
import xyz.qweru.geo.client.module.specific.ModuleGunColony
import xyz.qweru.geo.client.module.misc.ModuleScaffold
import xyz.qweru.geo.client.module.misc.ModuleTeams
import xyz.qweru.geo.client.module.move.*
import xyz.qweru.geo.client.module.player.ModuleFastUse
import xyz.qweru.geo.client.module.player.ModuleKeyAction
import xyz.qweru.geo.client.module.player.ModuleMine
import xyz.qweru.geo.client.module.visual.ModuleViewModel
import xyz.qweru.geo.client.module.world.ModuleChestStealer
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
        add(ModuleAutoAim())
        add(ModuleSwap())
        add(ModuleKeyAction())
        add(ModuleAnchorMacro())
        add(ModuleAutoTotem())
        add(ModuleViewModel())
        add(ModuleSprint())
        add(ModuleTargetStrafe())
        add(ModuleSpeed())
        add(ModuleBacktrack())
        add(ModuleAutoBlock())
        add(ModuleTeams())
        add(ModuleNoSlow())
        add(ModuleRotation())
        add(ModuleKillAura())
        add(ModuleTarget())
        add(ModuleFastStop())
        add(ModuleGunColony())
        add(ModuleAxeSwap())
        add(ModulePhaseWalk())
        add(ModuleLimiter())
        add(ModuleFastProjectile())
        add(ModulePacketLogger())
        add(ModulePacket())
        add(ModuleCCBlueX())

        // todo finish

//        add(ModuleVulcanElytra())
//        add(ModuleScaffold())
//        add(ModuleSafeAnchor())
//        add(ModuleMine())
        add(ModuleChestStealer())

        sorted.sortWith(Comparator.comparing(Module::name))
    }

    fun get(name: String): Module? = sorted.find { it.name == name }

    override fun add(system: System) {
        if (system !is Module) throw IllegalArgumentException()
        sorted.add(system)
        super.add(system)
    }

    override fun loadThis(json: JsonObject) {}
    override fun saveThis(json: JsonObject) {}
}