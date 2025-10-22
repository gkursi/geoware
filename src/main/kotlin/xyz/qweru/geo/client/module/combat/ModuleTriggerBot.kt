package xyz.qweru.geo.client.module.combat

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import org.lwjgl.glfw.GLFW
import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.client.helper.input.GameInput
import xyz.qweru.geo.client.helper.player.AttackHelper
import xyz.qweru.geo.client.helper.timing.TimerDelay
import xyz.qweru.geo.client.module.move.ModuleSprint
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.manager.combat.Attack
import xyz.qweru.geo.core.manager.combat.CombatState
import xyz.qweru.geo.core.manager.combat.TargetTracker
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.target
import xyz.qweru.geo.extend.thePlayer
import xyz.qweru.geo.extend.theWorld
import xyz.qweru.multirender.api.API
import java.util.*

/**
 * TODO: move away from using simulated mouse clicks for attacking
 */
class ModuleTriggerBot : Module("TriggerBot", "Automatically hit entities when hovering them", Category.COMBAT) {
    val sGeneral = settings.group("General")
    val sFailAttack = settings.group("Fail Attack")
    val sTech = settings.group("Auto Tech")

    val playerWeaponOnly by sGeneral.boolean("Player Weapon Only", "Only attack players with a weapon", true)
    val attackFirst by sGeneral.boolean("Require Target", "Requires you to attack the player manually before tbotting", false)
    val delay by sGeneral.longRange("Delay", "Attack delay", 0L..1L, 0L..500L)
    val miss by sGeneral.float("Miss%", "Chance of missing an attack", 0f, 0f, 0.9f)
    val awaitCrit by sGeneral.boolean("Await Crit", "Don't attack if a crit will be possible", true)
    val groundTicks by sGeneral.int("Ground Ticks", "Time to wait for crits after landing", 5, 0, 20)
        .visible { awaitCrit }
    val sprintCrit by sGeneral.boolean("Sprint Crit", "Don't reset on crit", false)
    val itemCooldown by sGeneral.float("Cooldown", "Vanilla item cooldown required to attack", 1f, 0f, 1f)
    val sprintReset by sGeneral.boolean("Sprint Reset", "Automatically resets sprint on hit", true)
    val inputTime by sTech.longRange("Input Time", "How long to press keys for when changing input", 100L..140L, 0L..1000L)

    val failAttack by sFailAttack.boolean("Fail Attack", "Try to attack (and fail) if the target is slightly out of reach", false)
    val failReach by sFailAttack.float("Fail Reach", "Extra reach for failing", 0.25f, 0.01f, 1f)
    val failChance by sFailAttack.float("Fail%", "Chance of failing an out-of-reach attack", 0.1f, 0f, 1f)

    val critDeflect by sTech.boolean("Crit Deflect", "Sprinthit the opponent right after they land a crit on you", true)
    val awaitDeflect by sTech.boolean("Await Deflect", "Don't hit until the crit has landed", false)
        .visible { critDeflect }

    val timer = TimerDelay()
    val random = Random()
    var nextDamage = Attack()
    var resetForward = false
    var moveForward = false
    var forwardTimer = TimerDelay()

    @Handler
    private fun onTick(e: PreTickEvent) {
        if (!inGame) return

        if (resetForward) {
            if (forwardTimer.hasPassed()) {
                resetForward = false
                mc.options.forwardKey.isPressed = GameInput.forwardKey
            }
            else mc.options.forwardKey.isPressed = moveForward
        }

        if (mc.currentScreen != null || !timer.hasPassed()) return
        nextDamage = Attack()
        if (mc.crosshairTarget is EntityHitResult) {
            val en = (mc.crosshairTarget as EntityHitResult).entity
            if (en is PlayerEntity && checkPlayer(en)) return
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

    fun checkPlayer(en: PlayerEntity): Boolean {
        if (!mc.thePlayer.activeItem.isEmpty) return true
        if (attackFirst && en != TargetTracker.target) return true

        if (TargetTracker.target == null) TargetTracker.target = en
        nextDamage = CombatState.TARGET.predictNextAttack()
        val nextAttack = CombatState.SELF.predictNextAttack()
        val sprinting = mc.thePlayer.isSprinting

        if (!AttackHelper.canAttack(en, playerWeaponOnly, cooldown = itemCooldown)) return true
        if (awaitCrit && AttackHelper.willCrit(groundTicks = groundTicks) && !nextAttack.crit) return true

        if (critDeflect && !sprinting && !nextAttack.crit) {
            if (CombatState.SELF.lastDamage.crit) {
                if (!mc.options.forwardKey.isPressed) {
                    GameInput.forwardKey = true
                    resetForward = true
                    moveForward = true
                    forwardTimer.reset(inputTime)
                }
                ModuleSprint.sprint(true, now = true)
            } else if (awaitDeflect && CombatState.TARGET.predictNextAttack().crit) {
                return true
            }
        }
        if (nextAttack.crit && mc.thePlayer.isSprinting) {
            ModuleSprint.sprint(false, now = true)
            if (sprintCrit) ModuleSprint.sprint(true) // start sprinting post-tick
        } else if (sprintReset && sprinting) {
            // this takes effect post tick
            ModuleSprint.sprint(false)
        }

        return false
    }
}