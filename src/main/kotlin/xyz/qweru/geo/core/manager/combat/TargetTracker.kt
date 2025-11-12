package xyz.qweru.geo.core.manager.combat

import net.minecraft.world.entity.player.Player
import xyz.qweru.geo.client.event.AttackFromPlayerEvent
import xyz.qweru.geo.client.event.AttackPlayerEvent
import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.client.module.misc.ModuleTeams
import xyz.qweru.geo.core.Global.mc
import xyz.qweru.geo.core.event.EventPriority
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.Systems
import xyz.qweru.geo.core.system.module.Modules
import xyz.qweru.geo.extend.minecraft.entity.inRange

/**
 * TODO global config for tracking conditions
 */
object TargetTracker {
    @Volatile
    var target: Player? = null
    lateinit var teams: ModuleTeams

    @Handler(priority = EventPriority.FIRST)
    private fun onAttackPlayer(e: AttackPlayerEvent) {
        target = e.player
    }

    @Handler(priority = EventPriority.FIRST)
    private fun onAttacked(e: AttackFromPlayerEvent) {
        if (e.player != mc.player) return
        target = e.source
    }

    @Handler(priority = EventPriority.FIRST)
    private fun onTick(e: PreTickEvent) {
        teams = Systems.get(Modules::class).get(ModuleTeams::class)
        if (target == null) return
        if (mc.level == null || mc.player == null || !target!!.inRange(64f))
            target = null
    }

    fun canTarget(player: Player): Boolean =
        !teams.enabled || !teams.isExempt(player)
}