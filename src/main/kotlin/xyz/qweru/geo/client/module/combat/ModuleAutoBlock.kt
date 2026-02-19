package xyz.qweru.geo.client.module.combat

import xyz.qweru.basalt.EventPriority
import xyz.qweru.geo.client.event.PacketSendEvent
import xyz.qweru.geo.client.event.PreCrosshairEvent
import xyz.qweru.geo.client.event.PreMoveSendEvent
import xyz.qweru.geo.client.helper.entity.TargetHelper
import xyz.qweru.geo.client.helper.inventory.InvHelper
import xyz.qweru.geo.client.helper.math.RangeHelper
import xyz.qweru.geo.client.helper.timing.TimerDelay
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.game.interaction.InteractionManager
import xyz.qweru.geo.core.system.impl.module.Category
import xyz.qweru.geo.core.system.impl.module.Module
import xyz.qweru.geo.extend.minecraft.network.isInteract

class ModuleAutoBlock : Module("AutoBlock", "Automatically block", Category.COMBAT) {

    val sg = settings.general
    val mode by sg.enum("Mode", "Auto block mode", Mode.LEGIT)
    val delay by sg.longRange("Delay", "Delay", 50L..100L, 0L..500L)

    val sc = settings.group("Conditions")
    val fov by sc.float("FOV", "Fov to block", 90f, 0f, 180f)
    val distance by sc.floatRange("Distance", "Required distance to the player", 0f..3.5f, 0f..8f)

    private var invert = false
    private val timer = TimerDelay()
    var blocking = false
        private set

    @Handler
    private fun preCrosshair(e: PreCrosshairEvent) {
        if (!inGame || mode != Mode.LEGIT) return
        invert = if (!blocking) {
            canBlock() && shouldBlock() && timer.hasPassed()
        } else {
            !canBlock() || !shouldBlock()
        }
    }

    fun unblock() {
        if (!blocking) return
        invert = true
    }

    @Handler
    private fun packetSend(e: PacketSendEvent) {
        if (!e.packet.isInteract) return
        invert = false
    }

    @Handler(priority = EventPriority.LAST)
    private fun postTick(e: PreMoveSendEvent) {
        if (!inGame || mode != Mode.LEGIT || !invert) return
        invert = false
        blocking = !blocking
        InteractionManager.useItem.client = blocking
        InteractionManager.useItem.applyClient()
        timer.reset(delay)
    }

    private fun shouldBlock(): Boolean =
        TargetHelper.findTarget(distance, RangeHelper.of(0f, 0f), fov) != null

    private fun canBlock(): Boolean = InvHelper.isInMainhand { InvHelper.isSword(it.item) }

    enum class Mode {
        LEGIT
    }
}