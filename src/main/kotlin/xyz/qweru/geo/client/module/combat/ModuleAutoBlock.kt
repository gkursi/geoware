package xyz.qweru.geo.client.module.combat

import xyz.qweru.basalt.EventPriority
import xyz.qweru.geo.client.helper.player.GameOptions
import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.client.helper.entity.TargetHelper
import xyz.qweru.geo.client.helper.math.RangeHelper
import xyz.qweru.geo.client.helper.inventory.InvHelper
import xyz.qweru.geo.client.helper.timing.TimerDelay
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module

class ModuleAutoBlock : Module("AutoBlock", "Automatically block", Category.COMBAT) {

    val sc = settings.group("Conditions")
    val fov by sc.float("FOV", "Fov to block", 90f, 0f, 180f)
    val distance by sc.floatRange("Distance", "Required distance to the player", 0f..3.5f, 0f..8f)

    val timer = TimerDelay()
    var blocking = false
        private set

    @Handler(priority = EventPriority.FIRST)
    private fun beforePreTick(e: PreTickEvent) {
        if (!inGame || !canBlock()) {
            if (blocking) blocking = false
            GameOptions.syncBind(GameOptions::useKey)
            return
        }
        blocking = shouldBlock() && timer.hasPassed()
        GameOptions.useKey = blocking
    }

    @Handler(priority = EventPriority.LAST)
    private fun afterPreTick(e: PreTickEvent) {
        if (!inGame || !canBlock()) return
        GameOptions.useKey = blocking
    }

    private fun shouldBlock(): Boolean =
        TargetHelper.findTarget(distance, RangeHelper.of(0f, 0f), fov) != null

    private fun canBlock(): Boolean = InvHelper.isInMainhand { InvHelper.isSword(it.item) }
}