package xyz.qweru.geo.client.module.combat

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.AxeItem
import net.minecraft.item.Items
import xyz.qweru.geo.client.event.GameRenderEvent
import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.client.helper.entity.TargetHelper
import xyz.qweru.geo.client.helper.player.InvHelper
import xyz.qweru.geo.client.helper.player.RotationHelper
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.manager.rotation.RotationHandler
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.inFov
import xyz.qweru.geo.extend.inRange
import xyz.qweru.geo.extend.theWorld
import xyz.qweru.multirender.api.API
import xyz.qweru.multirender.impl.mixin.mixininterface.MouseInvoker
import java.util.*

class ModuleAutoAim : Module("AutoAim", "Auto aim", Category.COMBAT) {
    val sg = settings.group("General")
    var fov by sg.float("FOV", "Field of view", 25f, 0f, 180f)
    var range by sg.floatRange("Range", "Range of target players", 1.5f..6f, 1f..15f)
    val silent by sg.boolean("Silent", "Silently aim", false)
    val hSpeed by sg.int("H Speed", "Horizontal camera speed", 30, 0, 360).visible { !silent }
    val vSpeed by sg.int("V Speed", "Horizontal camera speed", 0, 0, 360).visible { !silent }
    val random by sg.longRange("Random", "Randomization", -40L..40L, -100L..100L).visible { !silent }
    val weaponOnly by sg.boolean("Weapon Only", "Only aim when holding a weapon", true)
    val lock by sg.boolean("Lock Target", "Don't switch targets as long as the current target is in range", true)
    val invisible by sg.boolean("Invisible", "Allow targeting of completely invisible players (no armor & held item)", false)

    val rng = Random()
    var target: PlayerEntity? = null

    @Handler
    private fun onFrame(e: GameRenderEvent) {
        if (!inGame || mc.currentScreen != null || silent || !canTarget()) return

        val mod = rng.nextLong(random.start, random.endInclusive + 1) / 100
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

    @Handler
    private fun onTick(e: PreTickEvent) {
        if (!inGame || mc.currentScreen != null || !silent || !canTarget()) return
        val target = this.target ?: return
        RotationHandler.propose(RotationHelper.get(target), priority = 10)
    }

    fun canTarget(): Boolean {
        if (weaponOnly && !InvHelper.isInMainhand { InvHelper.isSword(it.item) || it.item is AxeItem || it.isOf(Items.MACE) }) return false
        if (target == null || !lock || !target!!.inRange(range) || !target!!.isAlive || target!!.world != mc.theWorld) {
            target = TargetHelper.findTarget(range, fov, invisible)
        }
        return (target != null && target!!.inFov(fov))
    }
}