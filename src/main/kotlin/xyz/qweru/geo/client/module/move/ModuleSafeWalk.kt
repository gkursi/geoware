package xyz.qweru.geo.client.module.move

import net.minecraft.util.PlayerInput
import xyz.qweru.geo.client.event.PostTickEvent
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.module.Category
import xyz.qweru.geo.core.module.Module
import xyz.qweru.geo.extend.thePlayer
import xyz.qweru.geo.extend.theWorld
import xyz.qweru.geo.helper.TimerDelay

class ModuleSafeWalk : Module("SafeWalk", "Don't fall off edges", Category.MOVEMENT) {
    val sg = settings.group("General")
    var sneak by sg.boolean("Sneak", "Also sneaks", false) // fixme
    var sneakDelay by sg.delay("Stand Delay", "Delay for un-sneaking", 50, 100, 0, 400)
        .visible { sneak }
    var minFall by sg.int("Min Fall", "Minimum possible fall distance for safewalking", 2, 1, 25)

    var wasSneaking = true
    var timer = TimerDelay()

    @Handler
    private fun preTick(e: PostTickEvent) {
        if (inGame && sneak && mc.thePlayer.isOnGround) {
            val input = mc.thePlayer.input.playerInput
            if (checkFall()) {
                mc.options.sneakKey.isPressed = true
                mc.thePlayer.input.playerInput = PlayerInput(input.forward(), input.backward(), input.left(), input.right(), input.jump(), true, input.sprint())
                wasSneaking = true
                timer.reset(sneakDelay.min, sneakDelay.max)
            } else if (wasSneaking) {
                if (timer.hasPassed()) {
                    mc.options.sneakKey.isPressed = false
                    mc.thePlayer.input.playerInput = PlayerInput(input.forward(), input.backward(), input.left(), input.right(), input.jump(), true, input.sprint())
                    wasSneaking = false
                }
            }
        }
    }

    fun checkFall(): Boolean {
        var pos = mc.thePlayer.blockPos
        for (i in 0..minFall) {
            pos = pos.down()
            if (!mc.theWorld.getBlockState(pos).isReplaceable) return false
        }
        return true
    }

}