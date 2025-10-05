package xyz.qweru.geo.client.module.combat

import xyz.qweru.geo.client.event.GameRenderEvent
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.module.Category
import xyz.qweru.geo.core.module.Module
import xyz.qweru.geo.helper.entity.TargetHelper
import xyz.qweru.geo.helper.player.RotationHelper
import xyz.qweru.multirender.api.API
import xyz.qweru.multirender.impl.mixin.mixininterface.MouseInvoker
import java.util.Random

class ModuleAimAssist : Module("AimAssist", "Auto aim", Category.COMBAT) {
    val sg = settings.group("General")
    var fov by sg.int("FOV", "Field of view", 360, 0, 360)
    var range by sg.float("Range", "Range of target players", 4f, 1f, 15f)
    val hSpeed by sg.int("H Speed", "Horizontal camera speed", 120, 0, 360)
    val vSpeed by sg.int("H Speed", "Horizontal camera speed", 120, 0, 360)
    val random by sg.delay("Random", "Ramdomization", -5, 25, -100, 100)

    val rng = Random()

    @Handler
    private fun onFrame(e: GameRenderEvent) {
        if (!inGame || mc.currentScreen != null) return
        val target = TargetHelper.findTarget(range, fov * 100000)
        if (target == null) return
        val mod = rng.nextLong(random.min, random.max) / 100
        val mouse = mc.mouse as MouseInvoker
        val delta = RotationHelper.getDelta(target)
        val td = API.base.getDeltaTime()
        val deltaX = delta[0].toDouble() * td * hSpeed
        val deltaY = delta[1].toDouble() * td * vSpeed
        mouse.setDeltaX(deltaX + (mod * deltaX))
        mouse.setDeltaY(deltaY + (mod * deltaY))
    }
}