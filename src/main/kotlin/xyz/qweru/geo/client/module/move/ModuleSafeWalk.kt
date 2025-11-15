package xyz.qweru.geo.client.module.move

import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.abstraction.game.GameOptions
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import xyz.qweru.geo.extend.minecraft.game.theLevel
import xyz.qweru.geo.client.helper.timing.TimerDelay

class ModuleSafeWalk : Module("SafeWalk", "Don't fall off edges", Category.MOVEMENT) {
    val sg = settings.group("General")
    var sneak by sg.boolean("Sneak", "Also sneaks", true)
    var standDelay by sg.longRange("Stand Delay", "Delay for un-sneaking", 50L..65L, 0L..400L)
        .visible { sneak }
    var minFall by sg.int("Min Fall", "Minimum possible fall distance for safewalking", 2, 1, 25)
    var minPitch by sg.int("Min Pitch", "Min pitch for safewalking (90 - straight down, -90 - straight up)", 35, -90, 90)

    var wasSneaking = true
    var timer = TimerDelay()

    @Handler
    private fun preTick(e: PreTickEvent) {
        if (inGame && sneak && mc.thePlayer.onGround() && !GameOptions.jumpKey) {
            if (check()) {
                GameOptions.sneakKey = true
                wasSneaking = true
                timer.reset(standDelay)
            } else if (wasSneaking && timer.hasPassed()) {
                GameOptions.syncBind(GameOptions::sneakKey)
                wasSneaking = false
            }
        }
    }

    fun check(): Boolean {
        if (mc.thePlayer.xRot < minPitch) return false
        var pos = mc.thePlayer.blockPosition()
        (0..minFall).forEach { _ ->
            if (!mc.theLevel.getBlockState(pos).canBeReplaced()) return false
            pos = pos.below()
        }
        return true
    }

}