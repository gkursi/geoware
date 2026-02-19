package xyz.qweru.geo.client.module.combat

import net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket
import net.minecraft.world.entity.PositionMoveRotation
import net.minecraft.world.phys.Vec3
import xyz.qweru.basalt.EventPriority
import xyz.qweru.geo.client.event.HandleTasksEvent
import xyz.qweru.geo.client.event.PacketReceiveEvent
import xyz.qweru.geo.client.helper.world.TrackedPosition
import xyz.qweru.geo.client.helper.world.inRange
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.event.PacketManagerFlushEvent
import xyz.qweru.geo.core.game.combat.CombatState
import xyz.qweru.geo.core.game.combat.TargetTracker
import xyz.qweru.geo.core.game.packet.PacketManager
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.kotlin.log.dbg
import xyz.qweru.geo.extend.kotlin.math.not
import xyz.qweru.geo.extend.minecraft.entity.pos
import xyz.qweru.geo.extend.minecraft.entity.relativeMotion
import xyz.qweru.geo.extend.minecraft.game.theLevel
import xyz.qweru.geo.extend.minecraft.game.thePlayer

class ModuleBacktrack : Module("Backtrack", "Simulates lag to give you an advantage", Category.COMBAT) {
    val sg = settings.general
    val sc = settings.group("Conditions")

    val requireTarget by sg.boolean("Require Target", "Requires a target to be active", true)
    val reset by sg.boolean("Reset", "Stop backtracking if none of the conditions are filled", true)
    val smartReset by sg.boolean("Smart Reset", "Stop backtracking if the real player is closer than the backtracked player", true)

    val always by sc.boolean("Always", "Always backtrack", false)
    val combo by sc.int("Combo", "Backtrack when you have this combo or more (0 = disabled)", 0, 0, 5)
    val backwards by sc.boolean("Backwards", "Backtrack when the target is moving backwards", false)
    val inRange by sc.boolean("In Reach", "Backtrack when the target is within reach", true)
    val range by sc.float("Range", "Range to backtrack", 2.5f, 0f, 6f)

    @Volatile
    private var trackedPosition: TrackedPosition? = null

    @Handler
    private fun onPacketProcess(e: HandleTasksEvent) {
        if (!inGame) return
        if (smartReset && TargetTracker.target?.let { mc.thePlayer.distanceToSqr(trackedPosition?.pos ?: Vec3.ZERO) < it.distanceToSqr(mc.thePlayer) } ?: false)
            PacketManager.forceFlush()
        if (reset && !trackedPosition?.inRange(range) && inRange) {
            logger.dbg("Flush: not in range (at $trackedPosition)")
            PacketManager.forceFlush()
        }
    }

    @Handler(priority = EventPriority.LAST)
    private fun onPacket(e: PacketReceiveEvent) {
        if (!inGame || !shouldBacktrack()) {
            return
        }

        PacketManager.lag()
        val packet = e.packet

        if (packet is ClientboundTeleportEntityPacket && packet.id == (TargetTracker.target?.id ?: -1)
            || packet is ClientboundMoveEntityPacket && packet.getEntity(mc.theLevel) == TargetTracker.target
            || packet is ClientboundEntityPositionSyncPacket && packet.id == (TargetTracker.target?.id ?: -1)) {

            TargetTracker.target?.let {
                if (trackedPosition != null) return
                trackedPosition = TrackedPosition()
                trackedPosition?.pos = it.pos
            }

            when (packet) {
                is ClientboundTeleportEntityPacket -> {
                    trackedPosition?.pos = PositionMoveRotation.calculateAbsolute(
                        PositionMoveRotation.of(TargetTracker.target!!),
                        packet.change(),
                        packet.relatives
                    ).position
                }

                is ClientboundMoveEntityPacket -> {
                    trackedPosition?.pos = packet.getEntity(mc.theLevel)?.pos ?: Vec3.ZERO
                    if (packet.hasPosition())
                        trackedPosition?.addDelta(packet.xa.toLong(), packet.ya.toLong(), packet.za.toLong())
                }

                is ClientboundEntityPositionSyncPacket -> {
                    trackedPosition?.pos = packet.values.position
                }

                else -> throw IllegalArgumentException()
            }

            logger.dbg("Tracked at ${trackedPosition?.pos} (rendering at ${TargetTracker.target!!.pos})")
        }
    }

    @Handler
    fun onFlush(e: PacketManagerFlushEvent) {
        trackedPosition = null
    }

    private fun shouldBacktrack(): Boolean {
        if (requireTarget && TargetTracker.target == null) return false
        return always
                || (CombatState.SELF.combo >= combo && combo > 0)
                || TargetTracker.target?.let {
                    it.relativeMotion.x < 0 && backwards
                            || trackedPosition.inRange(range) && inRange
                } ?: false
    }
}