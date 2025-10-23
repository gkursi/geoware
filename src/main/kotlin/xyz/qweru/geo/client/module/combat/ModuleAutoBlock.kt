package xyz.qweru.geo.client.module.combat

import net.minecraft.util.hit.HitResult
import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.client.helper.entity.TargetHelper
import xyz.qweru.geo.client.helper.input.GameInput
import xyz.qweru.geo.client.helper.player.InvHelper
import xyz.qweru.geo.client.helper.timing.TimerDelay
import xyz.qweru.geo.client.helper.world.WorldHelper
import xyz.qweru.geo.core.event.EventPriority
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.manager.combat.TargetTracker
import xyz.qweru.geo.core.system.Systems
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.core.system.module.Modules

class ModuleAutoBlock : Module("AutoBlock", "Automatically block", Category.COMBAT) {

    companion object {
        fun unblock() {
            val module = Systems.get(Modules::class).get(ModuleAutoBlock::class)
            if (!module.enabled || !module.timer.hasPassed()) return
            module.blocking = false
            module.logger.info("unblocked")
            module.timer.reset(module.blockDisable)
        }
    }

    val sg = settings.group("Conditions")
    val fov by sg.float("FOV", "Fov to block", 90f, 0f, 180f)
    val distance by sg.floatRange("Distance", "Required distance to the player", 0f..3.5f, 0f..8f)
    val raycast by sg.boolean("Raycast", "Raycast from the player to check if they can hit you", true)
    val assumeReach by sg.float("Assume Reach", "Raycast distance", 3.2f, 3f, 7f)
        .visible { raycast }

    val st = settings.group("Timing")
    val blockEnable by st.longRange("Block", "Speed at which to block after not blocking", 50L..75L, 0L..500L)
    val blockDisable by st.longRange("Unblock", "Speed at which to stop blocking", 50L..75L, 0L..500L)

    val timer = TimerDelay()
    var blocking = false
        private set

    @Handler(priority = EventPriority.FIRST)
    private fun beforePreTick(e: PreTickEvent) {
        if (!inGame || !canBlock()) {
            if (blocking) blocking = false
            mc.options.useKey.isPressed = GameInput.useKey
            return
        }
        blocking = shouldBlock() && timer.hasPassed()
    }

    @Handler(priority = EventPriority.LAST)
    private fun afterPreTick(e: PreTickEvent) {
        if (!inGame || !canBlock()) {
            return
        }
        mc.options.useKey.isPressed = blocking
        if (!blocking) logger.info("applied unblocked")
    }

    private fun shouldBlock(): Boolean {
        val target = TargetHelper.findTarget(distance, fov) ?: return false
        logger.info("Target: ${target.gameProfile.name}")
        return if (raycast) (WorldHelper.getCrosshairTarget(target, assumeReach.toDouble())
            ?.type == HitResult.Type.ENTITY) else true
    }

    private fun canBlock(): Boolean = InvHelper.isInMainhand { InvHelper.isSword(it.item) }
}