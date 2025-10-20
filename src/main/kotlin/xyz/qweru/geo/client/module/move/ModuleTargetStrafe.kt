package xyz.qweru.geo.client.module.move

import net.minecraft.entity.player.PlayerEntity
import xyz.qweru.geo.client.event.AttackPlayerEvent
import xyz.qweru.geo.client.event.PlayerAttackPlayerEvent
import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.manager.combat.CombatState
import xyz.qweru.geo.core.manager.combat.TargetTracker
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.inFov
import xyz.qweru.geo.extend.inRange
import xyz.qweru.geo.helper.input.GameInput
import xyz.qweru.geo.helper.timing.TimerDelay

class ModuleTargetStrafe : Module("TargetStrafe", "Automatically strafe around the target", Category.MOVEMENT) {
    val sg = settings.group("General")

    val minCombo by sg.int("Min Combo", "Min combo to start strafing", 0, 0, 4)
    val minDistance by sg.float("Min Distance", "Min distance to start strafing", 5f, 1f, 8f)
    val fov by sg.float("FOV", "Only strafe when the target is in FOV", 360f, 0f, 360f)
    val time by sg.longRange("Hold Time", "How long to hold in each direction", 550L..750L, 0L..2000L)
    val hitInvert by sg.boolean("Hit Invert", "Switch movement direction on attack", false)
    val damageInvert by sg.boolean("Damage Invert", "Switch movement direction on damage", true)
    val pauseScreen by sg.boolean("Pause Screen", "Pause while a screen is opened", true)

    private val holdTime = TimerDelay()
    private var key = Key.LEFT
    private var reset = false

    @Handler
    private fun onTick(e: PreTickEvent) {
        if (!inGame) return
        if (TargetTracker.target == null || CombatState.SELF.combo < minCombo || !validTarget(TargetTracker.target!!) || (pauseScreen && mc.currentScreen != null)) {
            checkReset()
            return
        }
        if (holdTime.hasPassed()) {
            swapKey()
        }
    }

    private fun validTarget(target: PlayerEntity) = target.inRange(minDistance) && target.inFov(fov)

    private fun checkReset() {
        if (!reset) return
        mc.options.leftKey.isPressed = GameInput.leftKey
        mc.options.rightKey.isPressed = GameInput.rightKey
        holdTime.reset(time)
        reset = false
    }

    @Handler
    private fun onAttack(e: AttackPlayerEvent) {
        if (hitInvert) swapKey()
    }

    @Handler
    private fun onAttacked(e: PlayerAttackPlayerEvent) {
        if (damageInvert) swapKey()
    }

    private fun swapKey() {
        holdTime.reset(time)
        key = when (key) {
            Key.LEFT -> {
                GameInput.leftKey = false
                GameInput.rightKey = true
                Key.RIGHT
            }

            Key.RIGHT -> {
                GameInput.leftKey = true
                GameInput.rightKey = false
                Key.LEFT
            }
        }
        reset = true
    }

    enum class Key {
        LEFT, RIGHT
    }
}