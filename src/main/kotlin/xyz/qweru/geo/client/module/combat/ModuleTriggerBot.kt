package xyz.qweru.geo.client.module.combat

import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import org.lwjgl.glfw.GLFW
import xyz.qweru.geo.client.event.PostTickEvent
import xyz.qweru.geo.client.module.move.ModuleSprint
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.getRelativeVelocity
import xyz.qweru.geo.extend.target
import xyz.qweru.geo.extend.thePlayer
import xyz.qweru.geo.extend.theWorld
import xyz.qweru.geo.helper.player.AttackHelper
import xyz.qweru.geo.helper.timing.TimerDelay
import xyz.qweru.multirender.api.API
import java.util.Random

/**
 * TODO: move away from using simulated mouse clicks for attacking
 */
class ModuleTriggerBot : Module("TriggerBot", "Automatically hit entities when hovering them", Category.COMBAT) {
    val sGeneral = settings.group("General")
    val sFailAttack = settings.group("Fail Attack")
    val sPickHit = settings.group("Pick Hit")

    val playerWeaponOnly by sGeneral.boolean("Player Weapon Only", "Only attack players with a weapon", true)
    val delay by sGeneral.longRange("Delay", "Attack delay", 55L..100L, 0L..500L)
    val miss by sGeneral.float("Miss%", "Chance of missing an attack", 0.1f, 0f, 0.9f)
    val awaitCrit by sGeneral.boolean("Await Crit", "Don't attack if a crit is possible", false)
    val awaitJump by sGeneral.boolean("Await Jump", "Don't attack before the jump cooldown is over", true).visible { awaitCrit }
    val itemCooldown by sGeneral.float("Cooldown", "Vanilla item cooldown required to attack", 0.9f, 0f, 1f)
    val sprintReset by sGeneral.boolean("Sprint Reset", "Automatically resets sprint on hit", true)

    val failAttack by sFailAttack.boolean("Fail Attack", "Try to attack (and fail) if the target is slightly out of reach", true)
    val failReach by sFailAttack.float("Fail Reach", "Extra reach for failing", 0.25f, 0.01f, 1f)
    val failChance by sFailAttack.float("Fail%", "Chance of failing an out-of-reach attack", 0.1f, 0f, 1f)

    val pickHit by sPickHit.boolean("Pick Hit", "Hit enemies without fully recharging when they are almost out of reach", true)
    val pickReach by sPickHit.float("Pick Reach%", "Min reach for doing a pick hit", 0.9f, 0.1f, 1f)
    val pickCharge by sPickHit.float("Pick Charge", "Min pick charge", 0.3f, 0f, 1f)
    val onlyBackwards by sPickHit.boolean("Only Backwards", "Only pick hit when you are moving backwards", true)

    val timer = TimerDelay()
    val random = Random()

    @Handler
    private fun onTick(e: PostTickEvent) {
        if (!inGame || mc.currentScreen != null || !timer.hasPassed()) return
        if (mc.crosshairTarget is EntityHitResult) {
            val en = (mc.crosshairTarget as EntityHitResult).entity
            val pickHit = canPickHit(mc.crosshairTarget!!.pos)

            if (!pickHit && !AttackHelper.canAttack(en, playerWeaponOnly, cooldown = itemCooldown)) return
            if (!mc.thePlayer.activeItem.isEmpty) return
            if (!pickHit && awaitCrit && AttackHelper.willCrit(awaitJump = awaitJump) && !AttackHelper.canCrit()) return

            if (sprintReset) ModuleSprint.reset()

            if (random.nextFloat() > miss) {
                API.mouseHandler.press(GLFW.GLFW_MOUSE_BUTTON_1)
                API.mouseHandler.release(GLFW.GLFW_MOUSE_BUTTON_1)
            }
            timer.reset(delay)
        } else if (failAttack && mc.crosshairTarget != null && mc.crosshairTarget!!.type == HitResult.Type.MISS) {
            if (random.nextFloat() > failChance) return
            val hit = mc.theWorld.target(mc.thePlayer.entityInteractionRange + failReach)
            if (hit !is EntityHitResult) return
            
            API.mouseHandler.press(GLFW.GLFW_MOUSE_BUTTON_1)
            API.mouseHandler.release(GLFW.GLFW_MOUSE_BUTTON_1)
            timer.reset(delay)
        }
    }

    fun canPickHit(hit: Vec3d): Boolean =
        pickHit && !(onlyBackwards && mc.thePlayer.getRelativeVelocity().x >= 0f)
                && mc.thePlayer.getAttackCooldownProgress(0.5f) >= pickCharge
                && mc.thePlayer.eyePos.squaredDistanceTo(hit) >= MathHelper.square(pickReach * mc.thePlayer.entityInteractionRange)
}