package xyz.qweru.geo.core.manager.movement

import net.minecraft.client.player.LocalPlayer
import net.minecraft.network.protocol.game.ServerboundClientTickEndPacket
import xyz.qweru.geo.abstraction.network.ClientConnection
import xyz.qweru.geo.core.Global.mc
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import xyz.qweru.geo.client.helper.timing.Timer
import xyz.qweru.geo.imixin.ILocalPlayer

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
        ClientConnection.sendPacket(ServerboundClientTickEndPacket.INSTANCE)

        canSendPackets = false
        tickingMovement = false
        tickTimer.reset()
    }

    private fun tickPlayer(p: LocalPlayer) {
        p.aiStep()
        (p as ILocalPlayer).geo_tickMovementPackets()
    }

}