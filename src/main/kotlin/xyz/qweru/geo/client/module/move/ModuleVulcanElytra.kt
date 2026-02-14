package xyz.qweru.geo.client.module.move

import xyz.qweru.geo.client.event.PostCrosshairEvent
import xyz.qweru.geo.client.event.PostMovementTickEvent
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.game.movement.MovementTicker
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.minecraft.game.thePlayer

class ModuleVulcanElytra : Module("VulcanElytra","Fly without rockets", Category.MOVEMENT) {
    val sg = settings.general
    val moveTick by sg.int("Move Tick", "Move tick speed", 19, 1, 20)
    val glideTick by sg.int("Glide Tick", "Glide ticks to apply strafe for", 1, 0, 10)

    val sb = settings.group("Boost")
    val hBoost by sb.float("Horizonal Boost", "Horizontal boost", 1f, 0f, 5f)

    var glideTicks = 0
    val gliding
        get() = glideTicks in 1..glideTick

    @Handler
    private fun crosshair(e: PostCrosshairEvent) {
        MovementTicker.tickSpeed = if (gliding) moveTick else 20
    }

    @Handler
    private fun onVelocity(event: PostMovementTickEvent) {
        if (mc.thePlayer.isFallFlying)
            glideTicks++
        else
            glideTicks = 0

        if (gliding) {
            event.setStrafe(hBoost)
        }
    }
}