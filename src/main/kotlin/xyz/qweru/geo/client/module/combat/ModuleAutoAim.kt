package xyz.qweru.geo.client.module.combat

import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.AxeItem
import net.minecraft.world.item.Items
import xyz.qweru.geo.client.event.GameRenderEvent
import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.client.helper.entity.TargetHelper
import xyz.qweru.geo.client.helper.player.inventory.InvHelper
import xyz.qweru.geo.client.helper.player.RotationHelper
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.game.rotation.Rotation
import xyz.qweru.geo.core.game.rotation.RotationHandler
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.minecraft.entity.inFov
import xyz.qweru.geo.extend.minecraft.entity.inRange
import xyz.qweru.geo.extend.minecraft.game.theLevel
import xyz.qweru.geo.extend.minecraft.item.isOf
import xyz.qweru.multirender.api.API
import java.util.*

class ModuleAutoAim : Module("AutoAim", "Auto aim", Category.COMBAT) {
    val sg = settings.group("General")
    val st = settings.group("Target")

    val silent by sg.boolean("Silent", "Silently aim", false)
    val hSpeed by sg.int("H Speed", "Horizontal camera speed", 30, 0, 360).visible { !silent }
    val vSpeed by sg.int("V Speed", "Horizontal camera speed", 0, 0, 360).visible { !silent }
    val random by sg.longRange("Random", "Randomization", -40L..40L, -100L..100L).visible { !silent }
    val weaponOnly by sg.boolean("Weapon Only", "Only aim when holding a weapon", true)
    val target by sg.enum("Target", "Which point to target", RotationHelper.TargetPoint.BODY)

    var range by st.floatRange("Range", "Range of target players", 1.5f..6f, 1f..15f)
    var wallRange by st.floatRange("Wall Range", "Range of target players", 0f..0f, 1f..15f)
    var fov by st.float("FOV", "Field of view", 25f, 0f, 180f)
    val lock by st.boolean("Lock Target", "Don't switch targets as long as the current target is in range", true)
    val invisible by st.boolean("Invisible", "Allow targeting of completely invisible players (no armor & held item)", false)

    val rng = Random()
    var currentTarget: Player? = null

    @Handler
    private fun onFrame(e: GameRenderEvent) {
        if (!inGame || mc.screen != null || silent || !canTarget()) return

        val mod = rng.nextLong(random.start, random.endInclusive + 1) / 100
        val delta = RotationHelper.getDelta(currentTarget!!, target)
        val td = API.base.getDeltaTime()
        val gcd = RotationHelper.gcd()

        var deltaX = delta[0].toDouble() * td * hSpeed
        deltaX = deltaX + (mod * deltaX)
        deltaX = deltaX - (deltaX % gcd)

        var deltaY = delta[1].toDouble() * td * vSpeed
        deltaY = deltaY + (mod * deltaY)
        deltaY = deltaY - (deltaY % gcd)

        API.mouseHandler.move(deltaX, deltaY)
    }

    @Handler
    private fun onTick(e: PreTickEvent) {
        if (!inGame || mc.screen != null || !silent || !canTarget()) return
        val target = this.currentTarget ?: return
        RotationHandler.propose(RotationHelper.get(target, point = this.target), Rotation.NOT_IMPORTANT_ATTACK)
    }

    fun canTarget(): Boolean {
        if (weaponOnly && !InvHelper.isInMainhand { InvHelper.isSword(it.item) || it.item is AxeItem || it.isOf(Items.MACE) }) return false
        if (currentTarget == null || !lock || !currentTarget!!.inRange(range) || !currentTarget!!.isAlive || currentTarget!!.level() != mc.theLevel) {
            currentTarget = TargetHelper.findTarget(range, wallRange, fov, invisible)?.player
        }
        return (currentTarget != null && currentTarget!!.inFov(fov))
    }
}