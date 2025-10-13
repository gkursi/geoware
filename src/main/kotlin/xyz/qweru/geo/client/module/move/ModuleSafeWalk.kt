package xyz.qweru.geo.client.module.move

import net.minecraft.util.PlayerInput
import xyz.qweru.geo.client.event.PostTickEvent
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.thePlayer
import xyz.qweru.geo.extend.theWorld
import xyz.qweru.geo.helper.timing.TimerDelay

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
    private fun preTick(e: PostTickEvent) {
        if (inGame && sneak && mc.thePlayer.isOnGround) {
            val input = mc.thePlayer.input.playerInput
            if (check()) {
                mc.options.sneakKey.isPressed = true
                mc.thePlayer.input.playerInput = PlayerInput(input.forward(), input.backward(), input.left(), input.right(), input.jump(), true, input.sprint())
                wasSneaking = true
                timer.reset(standDelay)
            } else if (wasSneaking) {
                if (timer.hasPassed()) {
                    mc.options.sneakKey.isPressed = false
                    mc.thePlayer.input.playerInput = PlayerInput(input.forward(), input.backward(), input.left(), input.right(), input.jump(), true, input.sprint())
                    wasSneaking = false
                }
            }
        }
    }

    fun check(): Boolean {
        if (mc.thePlayer.pitch < minPitch) return false
        var pos = mc.thePlayer.blockPos
        (0..minFall).forEach { _ ->
            if (!mc.theWorld.getBlockState(pos).isReplaceable) return false
            pos = pos.down()
        }
        return true
    }

}