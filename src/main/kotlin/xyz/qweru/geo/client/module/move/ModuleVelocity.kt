package xyz.qweru.geo.client.module.move

import net.minecraft.network.protocol.game.ClientboundExplodePacket
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket
import net.minecraft.world.phys.Vec3
import org.lwjgl.glfw.GLFW
import xyz.qweru.geo.client.event.PacketReceiveEvent
import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.game.combat.CombatState
import xyz.qweru.geo.core.game.packet.PacketManager
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.minecraft.entity.isOnGround
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import xyz.qweru.geo.mixin.math.Vec3Accessor
import xyz.qweru.multirender.api.API
import xyz.qweru.multirender.api.input.Input

class ModuleVelocity : Module("Velocity", "Modify knockback", Category.MOVEMENT) {
    val sg = settings.general
    val sJr = settings.group("Conditions").visible { mode.has(Mode.JUMP) }

    var mode by sg.multiEnum("Modes", "Modes for velocity", Mode.JUMP, Mode.LATENCY)
    var explosions by sg.boolean("Explosions", "Remove explosion velocity", false)
    val hMod by sg.float("H Velocity", "Horizontal velocity", 1f, 0f..1f)
        .visible { mode.has(Mode.VANILLA) || mode.has(Mode.VULCAN) }
    val vMod by sg.float("V Velocity", "Vertical velocity", 1f, 0f..1f)
        .visible { mode.has(Mode.VANILLA) || mode.has(Mode.VULCAN) }

    var always by sJr.boolean("Always", "Always jump reset", false)
    var firstHit by sJr.boolean("First Hit", "Jump Reset on the first hit in an exchange", true)
        .visible { !always }
    var sprintHit by sJr.boolean("Sprint Hit", "Jump reset when getting sprint-hit", true)
        .visible { !always }
    var combo by sJr.int("Combo", "When combo count >= this (0 = disabled)", 2, 0, 5)
        .visible { !always }
    var pauseCombo by sJr.int("Pause Combo", "Don't reset when the combo against you >= this (0 = disabled)", 0, 0, 5)


    private var jump = false

    @Handler
    private fun onTick(e: PreTickEvent) {
        if (!inGame) return

        if (mode.has(Mode.JUMP) && jump) {
            jump = false
            API.keyboardHandler.input(GLFW.GLFW_KEY_SPACE, Input.CLICK)
        }
    }

    @Handler
    private fun onPacketReceive(e: PacketReceiveEvent) {
        if (!inGame) return
        when (val packet = e.packet) {
            is ClientboundExplodePacket -> {
                if (packet.playerKnockback.isEmpty || !explosions) return
                onKB(packet.playerKnockback.get(), e)
            }

            is ClientboundSetEntityMotionPacket -> {
                if (packet.id != mc.thePlayer.id) return
                onKB(packet.movement, e)
            }
        }
    }

    private fun onKB(vec: Vec3, e: PacketReceiveEvent) {
        val accessor = vec as Vec3Accessor
        for (mode in mode.getEnabled()) {
            when (mode) {
                Mode.VANILLA-> {
                    accessor.geo_setX(vec.x * hMod)
                    accessor.geo_setY(vec.y * vMod)
                    accessor.geo_setZ(vec.z * hMod)
                }

                Mode.VULCAN -> {
                    if (!mc.thePlayer.isFallFlying) {
                        continue
                    }
                    accessor.geo_setX(vec.x * hMod)
                    accessor.geo_setY(vec.y * vMod)
                    accessor.geo_setZ(vec.z * hMod)
                }

                Mode.LATENCY -> PacketManager.lag(onlyIfInactive = true)

                Mode.JUMP -> {
                    if (!mc.thePlayer.isOnGround || !checkConditions()) {
                        continue
                    }
                    jump = true
                }
            }
        }
    }

    private fun checkConditions(): Boolean =
        (always || (firstHit && CombatState.SELF.combo <= 1 && CombatState.TARGET.combo <= 1) || (sprintHit && CombatState.TARGET.lastAttack.sprint) || (combo != 0 && CombatState.SELF.combo >= combo))
        && !(pauseCombo > 0 && CombatState.TARGET.combo >= pauseCombo)

    enum class Mode {
        VANILLA, JUMP, VULCAN, LATENCY
    }
}