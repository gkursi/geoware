package xyz.qweru.geo.client.module.move

import net.minecraft.network.protocol.game.ClientboundExplodePacket
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket
import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3
import org.lwjgl.glfw.GLFW
import xyz.qweru.geo.client.event.PacketReceiveEvent
import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.core.Core
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.game.combat.CombatState
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import xyz.qweru.geo.mixin.entity.EntityVelocityUpdateS2CPacketAccessor
import xyz.qweru.geo.mixin.math.Vec3Accessor
import xyz.qweru.multirender.api.API
import xyz.qweru.multirender.api.input.Input

class ModuleVelocity : Module("Velocity", "Modify knockback", Category.MOVEMENT) {
    val sg = settings.group("General")
    val sJr = settings.group("Conditions").visible { mode == Mode.JUMP }
    val sv = settings.group("Vulcan").visible { mode == Mode.VULCAN }

    var mode by sg.enum("Mode", "Mode for velocity", Mode.JUMP)
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
        if (mode != Mode.JUMP) return
        if (mc.thePlayer.hurtTime == Core.mc.player!!.hurtDuration - 1 && canJump && mc.thePlayer.onGround() && checkConditions()) {
            API.keyboardHandler.input(GLFW.GLFW_KEY_SPACE, Input.CLICK)
            canJump = false
        } else canJump = true
    }

    @Handler
    private fun onPacketReceive(e: PacketReceiveEvent) {
        if (!inGame) return
        val packet = e.packet
        if (packet is ClientboundExplodePacket) {
            if (packet.playerKnockback.isEmpty || !explosions) return
            onKB(packet.playerKnockback.get(), e)
        } else if (packet is ClientboundSetEntityMotionPacket) {
            val acc = packet as EntityVelocityUpdateS2CPacketAccessor
            val vec = Vec3(packet.xa, packet.ya, packet.za)
            onKB(vec, e)
            val e = Mth.clamp(vec.x, -3.9, 3.9)
            val f = Mth.clamp(vec.y, -3.9, 3.9)
            val g = Mth.clamp(vec.z, -3.9, 3.9)
            acc.geo_setVelocityX((e * 8000.0).toInt())
            acc.geo_setVelocityY((f * 8000.0).toInt())
            acc.geo_setVelocityZ((g * 8000.0).toInt())
        }
    }

    private fun onKB(vec: Vec3, e: PacketReceiveEvent) {
        val accessor = vec as Vec3Accessor
        when (mode) {
            Mode.VANILLA-> {
                accessor.geo_setX(0.0)
                accessor.geo_setY(0.0)
                accessor.geo_setZ(0.0)
            }

            Mode.VULCAN -> {
                if (mc.thePlayer.isFallFlying) {
                    accessor.geo_setX(0.0)
                    accessor.geo_setY(0.0)
                    accessor.geo_setZ(0.0)
                    e.cancelled = true
                    return
                }
            }

            else -> {}
        }
    }

    private fun checkConditions(): Boolean =
        (always || (firstHit && CombatState.SELF.combo <= 1 && CombatState.TARGET.combo <= 1) || (sprintHit && CombatState.TARGET.lastAttack.sprint) || (combo != 0 && CombatState.SELF.combo >= combo))
        && !(pauseCombo > 0 && CombatState.TARGET.combo >= pauseCombo)

    enum class Mode {
        VANILLA, JUMP, VULCAN
    }
}