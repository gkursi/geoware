package xyz.qweru.geo.client.module.combat

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.boss.enderdragon.EndCrystal
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.AxeItem
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult
import org.lwjgl.glfw.GLFW
import xyz.qweru.geo.client.helper.player.GameOptions
import xyz.qweru.geo.client.event.PostMovementTickEvent
import xyz.qweru.geo.client.helper.entity.TargetHelper
import xyz.qweru.geo.client.helper.network.PacketHelper
import xyz.qweru.geo.client.helper.player.AttackConditions
import xyz.qweru.geo.client.helper.inventory.InvHelper
import xyz.qweru.geo.client.helper.timing.TimerDelay
import xyz.qweru.geo.client.module.move.ModuleSprint
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.game.combat.Attack
import xyz.qweru.geo.core.game.combat.CombatState
import xyz.qweru.geo.core.game.combat.TargetTracker
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.kotlin.log.dbg
import xyz.qweru.geo.extend.minecraft.entity.attackCharge
import xyz.qweru.geo.extend.minecraft.entity.isOnGround
import xyz.qweru.geo.extend.minecraft.entity.relativeMotion
import xyz.qweru.geo.extend.minecraft.game.theLevel
import xyz.qweru.geo.extend.minecraft.world.hit
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import xyz.qweru.multirender.api.API
import xyz.qweru.multirender.api.input.Input
import java.util.*

class ModuleTriggerBot : Module("TriggerBot", "Automatically hit entities when hovering them", Category.COMBAT) {
    val sGeneral = settings.group("General")
    val sFailAttack = settings.group("Fail Attack")
    val sCrit = settings.group("Crits")
    val sTarget = settings.group("Target")

    val simulateClick by sGeneral.boolean("Simulate Click", "Simulates clicks instead of using packets", true)
    val silentSwing by sGeneral.boolean("Silent Swing", "Don't swing visually", false)
        .visible { !simulateClick }
    val pauseUse by sGeneral.boolean("Pause On Eat", "Pause attacking while using an item", true)
    val weaponOnly by sGeneral.boolean("Weapon Only", "Only attack with a weapon", true)
    val attackFirst by sGeneral.boolean("Require Target", "Requires you to attack the player manually before tbotting", false)
    val delay by sGeneral.longRange("Delay", "Attack delay", 0L..1L, 0L..500L)
    val miss by sGeneral.float("Miss%", "Chance of missing an attack", 0f, 0f, 0.9f)
    val groundTicks by sGeneral.int("Ground Ticks", "Time to wait for crits after landing", 5, 0, 20)
        .visible { awaitCrit }
    val itemCooldown by sGeneral.float("Cooldown", "Vanilla item cooldown required to attack", 1f, 0f, 1f)
    val sprintReset by sGeneral.boolean("Sprint Reset", "Automatically resets sprint on hit", true)
    val noAxeCooldown by sTarget.boolean("Fast Axe", "Ignore item cooldown when holding an axe if the target is blocking", true)

    val failAttack by sFailAttack.boolean("Fail Attack", "Try to attack (and fail) if the target is slightly out of reach", false)
    val failReach by sFailAttack.float("Fail Reach", "Extra reach for failing", 0.25f, 0.01f, 1f)
    val failChance by sFailAttack.float("Fail%", "Chance of failing an out-of-reach attack", 0.1f, 0f, 1f)

    val awaitCrit by sCrit.boolean("Await Crit", "Don't attack if a crit will be possible", true)
    val autoCrit by sCrit.boolean("Auto Crit", "Automatically stop sprinting pre-crit", true)
    val requireFall by sCrit.boolean("Require Fall", "Requires falling", true)
    val exceptPunish by sCrit.boolean("Except Punish", "Ignore falling when moving backwards", true)

    val players by sTarget.boolean("Players", "Player targeting", true)
    val crystals by sTarget.boolean("Crystals", "Crystal targeting", true)
    val onlyGround by sTarget.boolean("Only Ground", "Only break crystals while on ground", true)

    val timer = TimerDelay()
    val random = Random()
    var nextDamage = Attack()

    @Handler
    private fun onTick(e: PostMovementTickEvent) {
        if (!inGame || mc.screen != null || !timer.hasPassed() || (pauseUse && !mc.thePlayer.useItem.isEmpty)) return

        nextDamage = Attack()
        val crosshair = mc.hitResult ?: return

        if (crosshair is EntityHitResult) {
            val en = crosshair.entity
            if (en is Player && checkPlayer(en)) return
            if (random.nextFloat() > miss) {
                attack(en)
            }
            timer.reset(delay)
        } else if (failAttack && crosshair.type == HitResult.Type.MISS) {
            if (random.nextFloat() > failChance) return
            val hit = mc.theLevel.hit(mc.thePlayer.entityInteractionRange() + failReach)
            if (hit !is EntityHitResult) return

            API.mouseHandler.input(GLFW.GLFW_MOUSE_BUTTON_1, Input.CLICK)
            timer.reset(delay)
        }
    }

    fun attack(en: Entity) {
        if (simulateClick) {
            API.mouseHandler.input(GLFW.GLFW_MOUSE_BUTTON_1, Input.CLICK)
        } else {
            PacketHelper.attackAndSwing(en, silentSwing)
            mc.thePlayer.resetAttackStrengthTicker()
        }
    }

    fun checkPlayer(en: Player): Boolean {
        if (!mc.thePlayer.useItem.isEmpty && !InvHelper.isInMainhand { InvHelper.isSword(it.item) }) return true
        if (attackFirst && en != TargetTracker.target) return true
        if (!TargetHelper.canTarget(en)) return true

        if (TargetTracker.target == null) TargetTracker.target = en
        nextDamage = CombatState.TARGET.predictNextAttack()
        val nextAttack = CombatState.SELF.predictNextAttack()

        if (!canAttack(en)) return true
        if (waitForCrit(nextAttack)) return true

        if (nextAttack.crit && nextAttack.sprint && autoCrit) {
            GameOptions.forwardKey = false
            return true
        } else if (sprintReset) {
            // this takes effect post tick
            ModuleSprint.sprint(false)
        }

        logger.dbg("next: $nextAttack, sprint: ${mc.thePlayer.isSprinting}")
        val entity = mc.thePlayer
        logger.dbg("conditions: fall: ${entity.fallDistance} > 0.075f && ground:${!entity.onGround()} && charge:${entity.attackCharge} > 0.9f && climb:${!entity.onClimbable()} && water:${!entity.isInWater} && vehicle:${!entity.isPassenger}")
        return false
    }

    fun canAttack(target: Entity): Boolean {
        if (target is EndCrystal) {
            return crystals && (!onlyGround || mc.thePlayer.isOnGround)
        }
        if (target is Player && !players) {
            return false
        }

        val item = InvHelper.getMainhand().item
        if (item is AxeItem && target is LivingEntity && target.isBlocking) {
            return true
        }
        if (item !is AxeItem && InvHelper.isSword(item) && weaponOnly) {
            return false
        }

        return mc.thePlayer.attackCharge >= itemCooldown
    }

    fun waitForCrit(nextAttack: Attack): Boolean =
        awaitCrit && AttackConditions.willCrit(groundTicks = groundTicks) && awaitPartialCrit() && !nextAttack.crit

    fun awaitPartialCrit(): Boolean = !requireFall || exceptPunish && mc.thePlayer.relativeMotion.x < 0
}