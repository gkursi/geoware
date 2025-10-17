package xyz.qweru.geo.core.manager.combat

import net.minecraft.entity.player.PlayerEntity
import xyz.qweru.geo.client.event.AttackPlayerEvent
import xyz.qweru.geo.client.event.PlayerAttackPlayerEvent
import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.core.Glob.mc
import xyz.qweru.geo.core.event.EventPriority
import xyz.qweru.geo.core.event.Handler

/**
 * TODO global config for tracking conditions
 */
object TargetTracker {
    @Volatile
    var target: PlayerEntity? = null

    @Handler(priority = EventPriority.FIRST)
    private fun onAttackPlayer(e: AttackPlayerEvent) {
        target = e.player
    }

    @Handler(priority = EventPriority.FIRST)
    private fun onAttacked(e: PlayerAttackPlayerEvent) {
        if (e.player != mc.player) return
        target = e.source
    }

    @Handler(priority = EventPriority.FIRST)
    private fun onTick(e: PreTickEvent) {
        if (target == null) return
        if (mc.world == null || mc.player == null || target!!.squaredDistanceTo(mc.player) > 64)
            target = null
    }
}