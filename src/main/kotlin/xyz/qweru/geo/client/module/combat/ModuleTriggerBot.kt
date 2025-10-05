package xyz.qweru.geo.client.module.combat

import net.minecraft.util.hit.EntityHitResult
import org.lwjgl.glfw.GLFW
import xyz.qweru.geo.client.event.PostTickEvent
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.module.Category
import xyz.qweru.geo.core.module.Module
import xyz.qweru.geo.extend.thePlayer
import xyz.qweru.geo.helper.AttackHelper
import xyz.qweru.geo.helper.TimerDelay
import xyz.qweru.multirender.api.API

class ModuleTriggerBot : Module("TriggerBot", "Automatically hit entities when hovering them", Category.COMBAT) {
    val sGeneral = settings.group("General")
    val playerWeaponOnly by sGeneral.boolean("Player Weapon Only", "Only attack players with a weapon", false)
    val delay by sGeneral.delay("Delay", "Attack delay", 45L, 65L, 0L, 500L)

    val timer = TimerDelay()

    @Handler
    private fun onTick(e: PostTickEvent) {
        if (!inGame) return
        if (mc.crosshairTarget is EntityHitResult && timer.hasPassed() && mc.currentScreen == null) {
            val en = (mc.crosshairTarget as EntityHitResult).entity
            if (!AttackHelper.canAttack(en, playerWeaponOnly)) return
            if (!mc.thePlayer.activeItem.isEmpty) {
                return
            }
            API.mouseHandler.press(GLFW.GLFW_MOUSE_BUTTON_1)
            API.mouseHandler.release(GLFW.GLFW_MOUSE_BUTTON_1)
            timer.reset(delay.min, delay.max)
        }
    }
}