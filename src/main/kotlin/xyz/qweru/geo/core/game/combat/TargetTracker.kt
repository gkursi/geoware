package xyz.qweru.geo.core.game.combat

import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Player
import xyz.qweru.geo.client.event.AttackFromPlayerEvent
import xyz.qweru.geo.client.event.AttackPlayerEvent
import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.client.module.config.ModuleTarget
import xyz.qweru.geo.client.module.misc.ModuleTeams
import xyz.qweru.geo.core.Core.mc
import xyz.qweru.geo.core.event.EventPriority
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.SystemCache
import xyz.qweru.geo.extend.minecraft.entity.inRange

/**
 * TODO global config for tracking conditions
 */
object TargetTracker {
    @Volatile
    var target: Player? = null
    val teams: ModuleTeams by SystemCache.getModule()
    val config: ModuleTarget by SystemCache.getModule()

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
        if (target == null) return
        if (mc.level == null || mc.player == null || !target!!.inRange(Mth.square(config.abandonRange)))
            target = null
    }
}