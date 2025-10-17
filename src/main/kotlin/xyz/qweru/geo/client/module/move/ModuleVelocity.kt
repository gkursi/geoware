package xyz.qweru.geo.client.module.move

import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import org.lwjgl.glfw.GLFW
import xyz.qweru.geo.client.event.PacketReceiveEvent
import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.core.Glob
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.manager.combat.CombatState
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.thePlayer
import xyz.qweru.geo.mixin.entity.EntityVelocityUpdateS2CPacketAccessor
import xyz.qweru.geo.mixin.game.Vec3dAccesor
import xyz.qweru.multirender.api.API

class ModuleVelocity : Module("Velocity", "Modify knockback", Category.MOVEMENT) {
    val sg = settings.group("General")
    val sJr = settings.group("Conditions").visible { mode == Mode.JUMP_RESET }

    var mode by sg.enum("Mode", "Mode for velocity", Mode.JUMP_RESET)
    var explosions by sg.boolean("Explosions", "Remove explosion velocity", false).visible { mode == Mode.VANILLA }

    var always by sJr.boolean("Always", "Always jump reset", false)
    var firstHit by sJr.boolean("First Hit", "Jump Reset on the first hit in an exchange", true)
    var sprintHit by sJr.boolean("Sprint Hit", "Jump reset when getting sprint-hit", true)
    var combo by sJr.int("Combo", "When combo count >= this (0 = disabled)", 2, 0, 5)
    var pauseCombo by sJr.int("Pause Combo", "Don't reset when the combo against you >= this (0 = disabled)", 2, 0, 5)

    var canJump = true

    @Handler
    private fun onTick(e: PreTickEvent) {
        if (!inGame) return
        if (mode != Mode.JUMP_RESET) return
        if (mc.thePlayer.hurtTime == Glob.mc.player!!.maxHurtTime - 1 && canJump && mc.thePlayer.isOnGround && checkConditions()) {
            API.keyboardHandler.press(GLFW.GLFW_KEY_SPACE)
            API.keyboardHandler.release(GLFW.GLFW_KEY_SPACE)
            canJump = false
        } else canJump = true
    }

    @Handler
    private fun onPacketReceive(e: PacketReceiveEvent) {
        if (!inGame) return
        val packet = e.packet
        if (packet is ExplosionS2CPacket) {
            if (packet.playerKnockback.isEmpty || !explosions) return
            onKB(packet.playerKnockback.get())
        } else if (packet is EntityVelocityUpdateS2CPacket) {
            val acc = packet as EntityVelocityUpdateS2CPacketAccessor
            val vec = Vec3d(packet.velocityX, packet.velocityY, packet.velocityZ)
            onKB(vec)
            val e = MathHelper.clamp(vec.x, -3.9, 3.9)
            val f = MathHelper.clamp(vec.y, -3.9, 3.9)
            val g = MathHelper.clamp(vec.z, -3.9, 3.9)
            acc.geo_setVelocityX((e * 8000.0).toInt())
            acc.geo_setVelocityY((f * 8000.0).toInt())
            acc.geo_setVelocityZ((g * 8000.0).toInt())
        }
    }

    private fun onKB(vec: Vec3d) {
        when (mode) {
            Mode.VANILLA -> {
                val accessor = vec as Vec3dAccesor
                accessor.geo_setX(0.0)
                accessor.geo_setY(0.0)
                accessor.geo_setZ(0.0)
            }

            else -> {}
        }
    }

    private fun checkConditions(): Boolean =
        (always || (firstHit && CombatState.SELF.combo <= 1 && CombatState.TARGET.combo <= 1) || (sprintHit && CombatState.TARGET.lastAttack.sprint) || (combo != 0 && CombatState.SELF.combo >= combo))
        && !(pauseCombo > 0 && CombatState.TARGET.combo >= pauseCombo)

    enum class Mode {
        VANILLA, JUMP_RESET,
    }
}