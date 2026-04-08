package xyz.qweru.geo.client.module.move

import xyz.qweru.geo.client.helper.player.GameOptions
import xyz.qweru.geo.client.event.PostMovementTickEvent
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.impl.module.Category
import xyz.qweru.geo.core.system.impl.module.Module
import xyz.qweru.geo.extend.minecraft.entity.relativeMotion
import xyz.qweru.geo.extend.minecraft.game.thePlayer

class ModuleFastStop : Module("FastStop", "Immediately stop moving on key release", Category.MOVEMENT) {
    private val sg = settings.general
    private val mode by sg.enum("Mode", "Mode stopping movement", Mode.INPUT)
    private val reduction by sg.float("Reduction", "Multiplier", 0.5f, 0f, 1f)
        .visible { mode == Mode.REDUCE }

    @Handler
    private fun postMove(e: PostMovementTickEvent) {
        if (!GameOptions.moving) mode.action.invoke(this, e)
    }

    enum class Mode(val action: ModuleFastStop.(PostMovementTickEvent) -> Unit) {
        INPUT({
            GameOptions.syncMovement()
            if (!GameOptions.moving) {
                val vel = mc.thePlayer.relativeMotion

                if (vel.x > 0.1) GameOptions.backKey = true
                else if (vel.x < -0.1) GameOptions.forwardKey = true
            }
        }),
        REDUCE({
            if (!GameOptions.moving) {
                it.velX *= reduction
                it.velZ *= reduction
            }
        })
    }
}