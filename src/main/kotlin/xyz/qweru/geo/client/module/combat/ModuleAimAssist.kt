package xyz.qweru.geo.client.module.combat

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.AxeItem
import net.minecraft.item.Items
import net.minecraft.util.math.MathHelper
import xyz.qweru.geo.client.event.GameRenderEvent
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.module.Category
import xyz.qweru.geo.core.module.Module
import xyz.qweru.geo.extend.thePlayer
import xyz.qweru.geo.helper.entity.TargetHelper
import xyz.qweru.geo.helper.player.HotbarHelper
import xyz.qweru.geo.helper.player.RotationHelper
import xyz.qweru.multirender.api.API
import xyz.qweru.multirender.impl.mixin.mixininterface.MouseInvoker
import java.util.*

class ModuleAimAssist : Module("AimAssist", "Auto aim", Category.COMBAT) {
    val sg = settings.group("General")
    var fov by sg.int("FOV", "Field of view", 25, 0, 360)
    var range by sg.float("Range", "Range of target players", 6f, 1f, 15f)
    val hSpeed by sg.int("H Speed", "Horizontal camera speed", 25, 0, 360)
    val vSpeed by sg.int("H Speed", "Horizontal camera speed", 25, 0, 360)
    val random by sg.delay("Random", "Randomization", -50, 45, -100, 100)
    val weaponOnly by sg.boolean("Weapon Only", "Only aim when holding a weapon", true)
    val lock by sg.boolean("Lock Target", "Don't switch targets as long as the current target is in range", true)
    val invisible by sg.boolean("Invisible", "Allow targeting of completely invisible players (no armor & held item)", false)

    val rng = Random()
    var target: PlayerEntity? = null

    @Handler
    private fun onFrame(e: GameRenderEvent) {
        if (!inGame || mc.currentScreen != null) return
        if (weaponOnly && !HotbarHelper.isInMainhand { HotbarHelper.isSword(it.item) || it.item is AxeItem || it.isOf(Items.MACE) }) return
        if (target == null || !lock || mc.thePlayer.squaredDistanceTo(target!!) > MathHelper.square(range) || !target!!.isAlive) target =
            TargetHelper.findTarget(range, fov, invisible)
        if (target == null) return

        val mod = rng.nextLong(random.min, random.max) / 100
        val delta = RotationHelper.getDelta(target!!)
        val td = API.base.getDeltaTime()
        val gcd = RotationHelper.gcd()
        var deltaX = delta[0].toDouble() * td * hSpeed
        deltaX = deltaX + (mod * deltaX)
        deltaX = deltaX - (deltaX % gcd)
        var deltaY = delta[1].toDouble() * td * vSpeed
        deltaY = deltaY + (mod * deltaY)
        deltaY = deltaY - (deltaY % gcd)

        val mouse = mc.mouse as MouseInvoker
        mouse.setDeltaX(deltaX)
        mouse.setDeltaY(deltaY)
    }
}