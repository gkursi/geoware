package xyz.qweru.geo.client.module.combat

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import xyz.qweru.geo.client.event.PreCrosshair
import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.client.helper.entity.Target
import xyz.qweru.geo.client.helper.entity.TargetHelper
import xyz.qweru.geo.client.helper.network.PacketHelper
import xyz.qweru.geo.client.helper.player.RotationHelper
import xyz.qweru.geo.client.helper.timing.TimerDelay
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.manager.rotation.Rotation
import xyz.qweru.geo.core.manager.rotation.RotationHandler
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.thePlayer

class ModuleKillAura : Module("KillAura", "Automatically attack players in range", Category.COMBAT) {
    private val sg = settings.group("General")
    private val st = settings.group("Timing")
    private val sc = settings.group("Conditions")

    private val rotation by sg.enum("Rotation", "When should we rotate", RotationMode.FRAME)

    private val delay by st.longRange("Delay", "Hit delay", 90L..105L, 0L..200L)
    private val charge by st.float("Charge", "Item charge", 1f, 0f, 1f)
    private val fastShieldDisable by st.boolean("Shield Disable", "Quickly disable shields", true)

    private val range by sc.floatRange("Range", "Range to attack people in", 0.0f..3.5f, 0.0f..6f)
    private val wallRange by sc.floatRange("Wall Range", "Range to attack people trough walls", 0f..0f, 0f..6f)
    private val fov by sc.float("FOV", "Target fov", 360f, 0f, 360f)
    private val invisible by sc.boolean("Invisible", "Attack fully invisible players", false)

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
        if (RotationHandler.isLookingAt(theRotation) && canAttack()) {
            attack(target?.player ?: return)
        }
    }

    @Handler
    private fun frame(e: PreCrosshair) {
        if (!inGame || rotation != RotationMode.FRAME) return
        rotate()
    }

    private fun attack(player: PlayerEntity) {
        PacketHelper.attackAndSwing(player)
        mc.thePlayer.attack(player)
        timer.reset(delay)
    }

    private fun canAttack(): Boolean {
        if (!timer.hasPassed()) return false
        val targetCharge =
            if (fastShieldDisable && target?.player?.activeItem?.isOf(Items.SHIELD) ?: false) 0f
            else charge
        return mc.thePlayer.getAttackCooldownProgress(0.5f) >= targetCharge
    }

    private fun rotate() {
        if (!inGame) return
        val target = this.target ?: return
        theRotation = RotationHelper.get(target)
        RotationHandler.propose(theRotation, 20)
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