package xyz.qweru.geo.client.module.move

import org.lwjgl.glfw.GLFW
import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.core.Glob
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.module.Category
import xyz.qweru.geo.core.module.Module
import xyz.qweru.geo.extend.thePlayer
import xyz.qweru.multirender.api.API

class ModuleJumpReset : Module("JumpReset", "Automatically jump-reset on knockback", Category.MOVEMENT) {

    var canJump = true

    @Handler
    private fun onTick(e: PreTickEvent) {
        if (!inGame) return
        if (mc.thePlayer.hurtTime == Glob.mc.player!!.maxHurtTime - 1 && canJump && mc.thePlayer.isOnGround) {
            API.keyboardHandler.press(GLFW.GLFW_KEY_SPACE)
            API.keyboardHandler.release(GLFW.GLFW_KEY_SPACE)
            canJump = false
        } else canJump = true
    }
}