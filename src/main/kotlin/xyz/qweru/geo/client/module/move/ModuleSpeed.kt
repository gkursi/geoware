package xyz.qweru.geo.client.module.move

import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket
import xyz.qweru.geo.client.event.PostMoveSendEvent
import xyz.qweru.geo.client.event.PostMovementTickEvent
import xyz.qweru.geo.client.helper.network.PacketHelper
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.minecraft.entity.airTicks
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import xyz.qweru.geo.extend.minecraft.world.withStrafe

class ModuleSpeed : Module("Speed", "bypass test", Category.MOVEMENT) {
    private val sg = settings.general

    private val mode by sg.enum("Mode", "Speed mode", Mode.VULCAN_LOWHOP)

    private val svulcan = settings.group("Vulcan LHop").visible { mode == Mode.VULCAN_LOWHOP }
    private val downVel by svulcan.float("Velocity", "Downwards velocity", -0.1f, -0.5f, 0.5f)
    private val airTick by svulcan.int("Air Tick", "Which air tick to use", 6, 1, 10)

    @Handler
    private fun onVelocity(e: PostMovementTickEvent) {
        when (mode) {
            Mode.VULCAN_LOWHOP -> vulcanLowHop(e)
            Mode.GRIM -> {
                val vel = mc.thePlayer.deltaMovement.withStrafe(0.03)
                e.velX += vel.x
                e.velZ += vel.z
            }
        }
    }

    @Handler
    fun postMoveSend(e: PostMoveSendEvent) {
        if (mode != Mode.GRIM) return

        PacketHelper.sendPacket(
            ServerboundPlayerCommandPacket(mc.thePlayer, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING)
        )
    }

    fun vulcanLowHop(e: PostMovementTickEvent) {
        if (mc.thePlayer.airTicks == airTick) {
            e.velY += downVel
        }
    }

    enum class Mode {
        VULCAN_LOWHOP,
        GRIM,
    }
}