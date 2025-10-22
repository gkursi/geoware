package xyz.qweru.geo.core.manager.movement

import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.network.packet.c2s.play.ClientTickEndC2SPacket
import xyz.qweru.geo.core.Global.mc
import xyz.qweru.geo.extend.thePlayer
import xyz.qweru.geo.client.helper.timing.Timer
import xyz.qweru.geo.imixin.IClientPlayerEntity

/**
 * Change the clients movement tick speed and nothing else.
 */
object MovementTicker {
    private var tickTimer = Timer()
    var tickSpeed = 20
    var canSendPackets = true
        private set
    var tickingMovement = false
        private set

    fun canTick(): Boolean {
        return tickSpeed == 20 // we can always tick at 20, since it's called at the usual timing
                || tickTimer.hasPassed(1000L / tickSpeed)
    }

    fun tick() {
        if (mc.player == null) return
        tickingMovement = true

        tickPlayer(mc.thePlayer)
        mc.networkHandler!!.sendPacket(ClientTickEndC2SPacket.INSTANCE)

        canSendPackets = false
        tickingMovement = false
        tickTimer.reset()
    }

    private fun tickPlayer(p: ClientPlayerEntity) {
        p.tickMovement()
        (p as IClientPlayerEntity).geo_tickMovementPackets()
    }

}