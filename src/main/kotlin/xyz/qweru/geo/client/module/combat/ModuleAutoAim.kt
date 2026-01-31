package xyz.qweru.geo.client.module.combat

import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.AxeItem
import net.minecraft.world.item.Items
import xyz.qweru.geo.client.event.GameRenderEvent
import xyz.qweru.geo.client.helper.entity.TargetHelper
import xyz.qweru.geo.client.helper.inventory.InvHelper
import xyz.qweru.geo.client.helper.player.RotationHelper
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.game.rotation.Rotation
import xyz.qweru.geo.core.game.rotation.RotationConfig
import xyz.qweru.geo.core.game.rotation.RotationHandler
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.minecraft.entity.inFov
import xyz.qweru.geo.extend.minecraft.entity.inRange
import xyz.qweru.geo.extend.minecraft.game.theLevel
import xyz.qweru.geo.extend.minecraft.item.isOf

class ModuleAutoAim : Module("AutoAim", "Auto aim", Category.COMBAT) {
    val sg = settings.general
    val st = settings.group("Target")

    val silent: Boolean by sg.boolean("Silent", "Silently aim", false)
        .onChange { rotations = RotationConfig(mouseFix = true, forceClient = !it.value) }
    val weaponOnly by sg.boolean("Weapon Only", "Only aim when holding a weapon", true)
    val target by sg.enum("Target", "Which point to target", RotationHelper.TargetPoint.BODY)

    var range by st.floatRange("Range", "Range of target players", 1.5f..6f, 1f..15f)
    var wallRange by st.floatRange("Wall Range", "Range of target players", 0f..0f, 1f..15f)
    var fov by st.float("FOV", "Field of view", 25f, 0f, 180f)
    val lock by st.boolean("Lock Target", "Don't switch targets as long as the current target is in range", true)
    val invisible by st.boolean("Invisible", "Allow targeting of completely invisible players (no armor & held item)", false)

    var currentTarget: Player? = null
    var rotations = RotationConfig(mouseFix = true, forceClient = !silent)

    @Handler
    private fun onFrame(e: GameRenderEvent) {
        if (!inGame || mc.screen != null || !canTarget()) return
        val target = this.currentTarget ?: return
        RotationHandler.propose(RotationHelper.get(target, point = this.target, config = rotations), Rotation.UNIMPORTANT_ATTACK)
    }

    fun canTarget(): Boolean {
        if (weaponOnly && !InvHelper.isInMainhand { InvHelper.isSword(it.item) || it.item is AxeItem || it.isOf(Items.MACE) }) return false
        if (currentTarget == null || !lock || !currentTarget!!.inRange(range) || !currentTarget!!.isAlive || currentTarget!!.level() != mc.theLevel) {
            currentTarget = TargetHelper.findTarget(range, wallRange, fov, invisible)?.player
        }
        return (currentTarget != null && currentTarget!!.inFov(fov))
    }
}