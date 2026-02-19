package xyz.qweru.geo.client.module.combat

import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Items
import net.minecraft.world.phys.EntityHitResult
import xyz.qweru.geo.client.event.PreCrosshairEvent
import xyz.qweru.geo.client.event.PreMoveSendEvent
import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.client.helper.entity.Target
import xyz.qweru.geo.client.helper.entity.TargetHelper
import xyz.qweru.geo.client.helper.network.PacketHelper
import xyz.qweru.geo.client.helper.player.RotationHelper
import xyz.qweru.geo.client.helper.timing.TimerDelay
import xyz.qweru.geo.client.helper.world.WorldHelper
import xyz.qweru.geo.client.module.move.ModuleSprint
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.game.rotation.Rotation
import xyz.qweru.geo.core.game.rotation.RotationHandler
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.minecraft.entity.attackCharge
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import xyz.qweru.geo.extend.minecraft.item.isOf
import java.util.function.Predicate

class ModuleKillAura : Module("KillAura", "Automatically attack players in range", Category.COMBAT) {
    private val sg = settings.general
    private val st = settings.group("Timing")
    private val sc = settings.group("Conditions")

    private val rotation by sg.enum("Rotation", "When should we rotate", RotationMode.FRAME)
    private val sprintReset by sg.boolean("Sprint Reset", "Reset sprint after hit", true)
    private val aimPoint by sg.enum("Aim Point", "Where should we aim", RotationHelper.TargetPoint.CLAMP)

    private val delay by st.longRange("Delay", "Hit delay", 90L..105L, 0L..200L)
    private val charge by st.float("Charge", "Item charge", 1f, 0f, 1f)
    private val fastShieldDisable by st.boolean("Shield Disable", "Quickly disable shields", true)
//    private val awaitCrit by st.boolean("Await Crit", "Wait for criticals", true)

    private val range by sc.floatRange("Range", "Range to attack people in", 0.0f..3.5f, 0.0f..6f)
    private val wallRange by sc.floatRange("Wall Range", "Range to attack people trough walls", 0f..0f, 0f..6f)
    private val fov by sc.float("FOV", "Target fov", 360f, 0f, 360f)
    private val invisible by sc.boolean("Invisible", "Attack fully invisible players", false)
    private val behindEntity by sg.boolean("Behind Entities", "Attack entities behind other entities", false)
    private val multitask by sg.boolean("Multitask", "Allow multitasking", false)

    private var target: Target? = null
    private var theRotation: Rotation = Rotation(0f, 0f)
    private val timer = TimerDelay()

    @Handler
    private fun preTick(e: PreTickEvent) {
        if (!inGame) return
        findTarget()

        if (rotation == RotationMode.TICK) {
            rotate()
        }
    }

    @Handler
    private fun preMovePacket(e: PreMoveSendEvent) {
        if (!canAttack() || !isLookingAtTarget()) return
        attack(target?.player ?: return)
    }

    @Handler
    private fun frame(e: PreCrosshairEvent) {
        if (!inGame || rotation != RotationMode.FRAME) return
        rotate()
    }

    private fun attack(player: Player) {
        PacketHelper.attackAndSwing(player)
        mc.thePlayer.resetAttackStrengthTicker()
        timer.reset(delay)

        if (sprintReset) {
            ModuleSprint.sprint(false)
        }
    }

    private fun canAttack(): Boolean {
        if (!timer.hasPassed()) return false
        val targetCharge =
            if (fastShieldDisable && target?.player?.useItem?.isOf(Items.SHIELD) ?: false) 0f
            else charge
        return mc.thePlayer.attackCharge >= targetCharge
    }

    private fun rotate() {
        if (!inGame) return
        if (!multitask && mc.thePlayer.isUsingItem) return
        val target = target ?: return

        theRotation = RotationHelper.get(target, point = aimPoint)
        RotationHandler.propose(theRotation, Rotation.ATTACK)
    }

    private fun isLookingAtTarget(): Boolean {
        val target = target ?: return false

        val hit = WorldHelper.getCrosshairTarget(
            range.endInclusive.toDouble(),
            RotationHandler.lastSentRot,
            wallRange = wallRange.endInclusive.toDouble(),
            filter = Predicate { entity ->
                return@Predicate if (behindEntity) {
                    entity == target
                } else {
                    true
                }
            }
        )

        return hit is EntityHitResult && hit.entity == target.player
    }

    private fun findTarget() {
        target = TargetHelper.findTarget(range, wallRange, fov, invisible)
    }

    enum class RotationMode {
        FRAME,
        TICK,
        NONE
    }

}