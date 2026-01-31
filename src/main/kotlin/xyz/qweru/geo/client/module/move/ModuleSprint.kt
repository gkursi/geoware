package xyz.qweru.geo.client.module.move

import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket
import xyz.qweru.basalt.EventPriority
import xyz.qweru.geo.client.helper.player.GameOptions
import xyz.qweru.geo.client.event.PacketSendEvent
import xyz.qweru.geo.client.event.PreMovementTickEvent
import xyz.qweru.geo.client.helper.timing.TimerDelay
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.SystemCache
import xyz.qweru.geo.core.system.Systems
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.core.system.module.Modules
import xyz.qweru.geo.extend.minecraft.game.thePlayer

class ModuleSprint : Module("Sprint", "Automatically sprint", Category.MOVEMENT) {

    companion object {
        val module: ModuleSprint by SystemCache.getModule()

        fun sprint(sprinting: Boolean, now: Boolean = false) {
            if (module.enabled) {
                module.sprinting = sprinting
                if (now) {
                    module.sprint(sprinting)
                }
            }
            else GameOptions.sprintKey = sprinting
        }
    }

    val sg = settings.general
    val mode by sg.enum("Mode", "Mode for applying sprint", Mode.LEGIT)
    val keyMode by sg.enum("Key", "Key to tap when resetting sprint", KeyMode.W)
    val resetTime by sg.longRange("Reset Time", "Delay between stopping and starting sprint", 50L..100L, 0L..1500L)
    val keyTime by sg.longRange("Key Time", "How long should we hold a key for", 10..40L, 0L..100L)
        .visible { keyMode != KeyMode.NONE }
    val awaitGround by sg.boolean("Await Ground", "Land before resetting sprint", false)
    val onlyKey by sg.boolean("Only Key", "Don't sprint at all", false)

    var sprinting = false
        set(value) {
            field = value
            if (!field) {
                resetDelay.reset(resetTime)
                keyDelay.reset(keyTime)
                waitForGround = awaitGround
                tap()
            }
        }
    private val resetDelay = TimerDelay()
    private val keyDelay = TimerDelay()
    private var waitForGround = false
    private var resetKey = false

    @Handler(priority = EventPriority.LAST)
    private fun onTick(e: PreMovementTickEvent) {
        if (!inGame) return
        if (shouldSprint() && resetDelay.hasPassed()) {
            sprinting = true
            waitForGround = false
        }
        if (mc.screen == null && sprinting != mc.thePlayer.isSprinting && !onlyKey) sprint(sprinting)
        if (resetKey && keyDelay.hasPassed()) {
            tap(false)
            resetKey = false
        }
    }

    fun shouldSprint(): Boolean = !(waitForGround && !mc.thePlayer.onGround()) && when (mode) {
        Mode.LEGIT -> GameOptions.forwardKey
        Mode.OMNI -> GameOptions.moving
    }

    @Handler
    private fun onPacketSend(e: PacketSendEvent) {
        val packet = e.packet
        if (packet !is ServerboundPlayerCommandPacket) return
        when (packet.action) {
            ServerboundPlayerCommandPacket.Action.START_SPRINTING -> sprinting = true
            ServerboundPlayerCommandPacket.Action.STOP_SPRINTING -> sprinting = false
            else -> {}
        }
    }

    private fun sprint(enable: Boolean) {
        GameOptions.sprintKey = enable
        mc.thePlayer.isSprinting = enable
        sprinting = enable
    }

    private fun tap(press: Boolean = true) {
        when (keyMode) {
            KeyMode.S -> {
                if (mc.options.keyDown.isDown != press) {
                    mc.options.keyDown.isDown = press
                }
            }
            KeyMode.W -> {
                if (mc.options.keyUp.isDown == press) {
                    mc.options.keyUp.isDown = !press && GameOptions.forwardKey
                }
            }
            KeyMode.NONE -> {}
        }
        resetKey = press
    }

    enum class Mode {
        LEGIT, OMNI
    }

    enum class KeyMode {
        NONE, S, W
    }

}