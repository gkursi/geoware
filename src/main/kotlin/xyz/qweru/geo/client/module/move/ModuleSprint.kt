package xyz.qweru.geo.client.module.move

import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
import xyz.qweru.geo.client.event.PacketSendEvent
import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.core.Glob.mc
import xyz.qweru.geo.core.event.EventPriority
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.Systems
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.core.system.module.Modules
import xyz.qweru.geo.extend.thePlayer
import xyz.qweru.geo.helper.network.PacketHelper
import xyz.qweru.geo.helper.timing.TimerDelay

class ModuleSprint : Module("Sprint", "Automatically sprint", Category.MOVEMENT) {

    companion object {
        fun reset() {
            val sprint = Systems.get(Modules::class).get(ModuleSprint::class)
            if (sprint.enabled) sprint.sprinting = false
            else mc.options.sprintKey.isPressed = false
        }
    }

    val sg = settings.group("General")
    val mode by sg.enum("Mode", "Mode for applying sprint", Mode.NORMAL)
    val resetTime by sg.longRange("Reset Time", "Delay between stopping and starting sprint", 200L..250L, 0L..1500L)
    val awaitGround by sg.boolean("Await Ground", "Land before resetting sprint", false)

    var sprinting = false
        set(value) {
            field = value
            if (!field) {
                resetDelay.reset(resetTime)
                waitForGround = awaitGround
            }
        }
    private val resetDelay = TimerDelay()
    private var waitForGround = false

    @Handler(priority = EventPriority.LAST)
    private fun onTick(e: PreTickEvent) {
        if (!inGame) return
        if (mc.options.forwardKey.isPressed && resetDelay.hasPassed() && !(waitForGround && !mc.thePlayer.isOnGround)) {
            sprinting = true
            waitForGround = false
        }
        if (sprinting != mc.thePlayer.isSprinting) sprint(sprinting)
    }

    @Handler
    private fun onPacketSend(e: PacketSendEvent) {
        val packet = e.packet
        if (packet !is ClientCommandC2SPacket) return
        when (packet.mode) {
            ClientCommandC2SPacket.Mode.START_SPRINTING -> sprinting = true
            ClientCommandC2SPacket.Mode.STOP_SPRINTING -> sprinting = false
            else -> {}
        }
    }

    private fun sprint(enable: Boolean) {
        when (mode) {
            Mode.NORMAL -> mc.options.sprintKey.isPressed = enable
            Mode.PACKET -> {
                PacketHelper.sendPacket(ClientCommandC2SPacket(mc.player,
                    if (enable) ClientCommandC2SPacket.Mode.START_SPRINTING
                    else ClientCommandC2SPacket.Mode.STOP_SPRINTING)
                )
            }
            Mode.CLIENT -> {}
        }
        mc.thePlayer.isSprinting = enable
        sprinting = enable
    }


    enum class Mode {
        NORMAL, PACKET, CLIENT
    }

}