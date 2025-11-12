package xyz.qweru.geo.client.module.combat

import net.minecraft.world.phys.HitResult
import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.client.helper.entity.TargetHelper
import xyz.qweru.geo.abstraction.game.GOptions
import xyz.qweru.geo.client.helper.math.RangeHelper
import xyz.qweru.geo.client.helper.player.inventory.InvHelper
import xyz.qweru.geo.client.helper.timing.TimerDelay
import xyz.qweru.geo.client.helper.world.WorldHelper
import xyz.qweru.geo.core.event.EventPriority
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.Systems
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.core.system.module.Modules
import xyz.qweru.geo.extend.minecraft.entity.rotation

class ModuleAutoBlock : Module("AutoBlock", "Automatically block", Category.COMBAT) {

    companion object {
        fun unblock() {
            val module = Systems.get(Modules::class).get(ModuleAutoBlock::class)
            if (!module.enabled || !module.timer.hasPassed()) return
            module.blocking = false
            module.timer.reset(module.blockDisable)
            GOptions.useKey = module.blocking
        }
    }

    val sc = settings.group("Conditions")
    val fov by sc.float("FOV", "Fov to block", 90f, 0f, 180f)
    val distance by sc.floatRange("Distance", "Required distance to the player", 0f..3.5f, 0f..8f)
    val raycast by sc.boolean("Raycast", "Raycast from the player to check if they can hit you", true)
    val assumeReach by sc.float("Assume Reach", "Raycast distance", 3.2f, 3f, 7f)
        .visible { raycast }

    val st = settings.group("Timing")
    val blockDisable by st.longRange("Unblock", "Speed at which to stop blocking", 50L..75L, 0L..500L)

    val timer = TimerDelay()
    var blocking = false
        private set

    @Handler(priority = EventPriority.FIRST)
    private fun beforePreTick(e: PreTickEvent) {
        if (!inGame || !canBlock()) {
            if (blocking) blocking = false
            GOptions.syncBind(GOptions::useKey)
            return
        }
        blocking = shouldBlock() && timer.hasPassed()
        GOptions.useKey = blocking
    }

    @Handler(priority = EventPriority.LAST)
    private fun afterPreTick(e: PreTickEvent) {
        if (!inGame || !canBlock()) return
        GOptions.useKey = blocking
    }

    private fun shouldBlock(): Boolean {
        val target = TargetHelper.findTarget(distance, RangeHelper.from(0f, 0f), fov)?.player ?: return false
        return if (raycast) (WorldHelper.getCrosshairTarget(target, assumeReach.toDouble(), rotation = target.rotation)
            ?.type == HitResult.Type.ENTITY) else true
    }

    private fun canBlock(): Boolean = InvHelper.isInMainhand { InvHelper.isSword(it.item) }
}