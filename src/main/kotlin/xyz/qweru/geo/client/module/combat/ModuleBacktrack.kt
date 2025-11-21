package xyz.qweru.geo.client.module.combat

import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket
import net.minecraft.network.protocol.game.ClientboundSoundPacket
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.PositionMoveRotation
import net.minecraft.world.phys.Vec3
import xyz.qweru.geo.client.helper.world.TrackedPosition
import xyz.qweru.geo.client.event.HandleTasksEvent
import xyz.qweru.geo.client.event.PacketReceiveEvent
import xyz.qweru.geo.client.helper.network.PacketHelper
import xyz.qweru.geo.core.event.EventPriority
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.game.combat.CombatState
import xyz.qweru.geo.core.game.combat.TargetTracker
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.minecraft.entity.relativeMotion
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import xyz.qweru.geo.client.helper.timing.TimerDelay
import xyz.qweru.geo.extend.kotlin.log.dbg
import xyz.qweru.geo.extend.minecraft.entity.inRange
import xyz.qweru.geo.extend.minecraft.entity.pos
import java.util.concurrent.ConcurrentLinkedQueue

class ModuleBacktrack : Module("Backtrack", "Simulates lag to give you extra reach", Category.COMBAT) {
    val sg = settings.group("General")
    val sc = settings.group("Conditions")

    val timing by sg.enum("Timing", "Timing for packets", Timing.BULK)
    val delay by sg.longRange("Delay", "Packet delay", 150L..250L, 0L..1500L)
    val requireTarget by sg.boolean("Require Target", "Requires a target to be active", true)
    val smartReset by sg.boolean("Smart Reset", "Stop backtracking if the real player is closer than the backtracked player", true)

    val always by sc.boolean("Always", "Always backtrack", false)
    val combo by sc.int("Combo", "Backtrack when you have this combo or more (0 = disabled)", 0, 0, 5)
    val backwards by sc.boolean("Backwards", "Backtrack when the target is moving backwards", false)
    val inRange by sc.boolean("In Reach", "Backtrack when the target is within reach", true)
    val range by sc.float("Range", "Range to backtrack", 2.5f, 0f, 6f)

    private val receivedPacketQueue = ConcurrentLinkedQueue<StoredPacket>()
    private val packetsToProcess = ConcurrentLinkedQueue<StoredPacket>()
    private val bulkDelay = TimerDelay()
    @Volatile
    private var trackedPosition: TrackedPosition? = null

    @Handler
    private fun onPacketProcess(e: HandleTasksEvent) {
        if (!inGame) {
            handleAll(false)
            packetsToProcess.clear()
            return
        }
        if (smartReset && TargetTracker.target?.let { mc.thePlayer.distanceToSqr(trackedPosition?.pos ?: Vec3.ZERO) < it.distanceToSqr(mc.thePlayer) } ?: false)
            handleAll()

        when (timing) {
            Timing.BULK -> {
                if (bulkDelay.hasPassed()) handleAll()
            }
            Timing.SINGLE -> receivedPacketQueue.removeIf {
                if (it.timer.hasPassed()) {
                    packetsToProcess.add(it)
                    return@removeIf true
                }
                return@removeIf false
            }
        }

        while (!packetsToProcess.isEmpty()) {
            val storedPacket = packetsToProcess.poll()
            PacketHelper.handlePacket(storedPacket.packet)
        }
    }

    @Handler(priority = EventPriority.LAST)
    private fun onPacket(e: PacketReceiveEvent) {
        if (!inGame || !shouldBacktrack()) {
            handleAll()
            return
        }
        val packet = e.packet
        // credit to liquidbounce
        when (packet) {
            // Ignore message-related packets
            is ClientboundPlayerChatPacket, is ClientboundSystemChatPacket -> {
                return
            }

            // Flush on teleport or disconnect
            is ClientboundPlayerPositionPacket, is ClientboundDisconnectPacket -> {
                handleAll()
                return
            }

            // Ignore own hurt sounds
            is ClientboundSoundPacket -> {
                if (packet.sound.value() == SoundEvents.PLAYER_HURT) {
                    return
                }
            }

            // Flush on own death
            is ClientboundSetHealthPacket -> {
                if (packet.health <= 0) {
                    handleAll()
                    return
                }
            }
        }

        if (packet is ClientboundTeleportEntityPacket && packet.id == (TargetTracker.target?.id ?: -1)
            || packet is ClientboundMoveEntityPacket && packet.getEntity(mc.level) == TargetTracker.target
            || packet is ClientboundEntityPositionSyncPacket && packet.id == (TargetTracker.target?.id ?: -1)) {

            TargetTracker.target?.let {
                if (trackedPosition != null) return
                trackedPosition = TrackedPosition()
                trackedPosition?.pos = it.pos
            }

            when (packet) {
                is ClientboundTeleportEntityPacket -> trackedPosition?.pos = PositionMoveRotation.calculateAbsolute(PositionMoveRotation.of(TargetTracker.target), packet.change(), packet.relatives).position
                is ClientboundMoveEntityPacket -> {
                    trackedPosition?.pos = packet.getEntity(mc.level)?.pos ?: Vec3.ZERO
                    if (packet.hasPosition())
                        trackedPosition?.addDelta(packet.xa.toLong(), packet.ya.toLong(), packet.za.toLong())
                }
                is ClientboundEntityPositionSyncPacket -> trackedPosition?.pos = packet.values.position
                else -> throw IllegalArgumentException()
            }

            logger.dbg("Tracked at ${trackedPosition?.pos} (rendering at ${TargetTracker.target!!.pos})")
        }

        receivedPacketQueue.add(StoredPacket(packet, TimerDelay().also { it.reset(delay) }))
        e.cancelled = true
    }

    private fun shouldBacktrack(): Boolean {
        if (requireTarget && TargetTracker.target == null) return false
        return always || (CombatState.SELF.combo >= combo && combo > 0) || TargetTracker.target?.let { target ->
            target.relativeMotion.x < 0 && backwards || target.inRange(range) && inRange
        } ?: false
    }

    private fun handleAll(sendPackets: Boolean = true) {
        bulkDelay.reset(delay)
        if (sendPackets) {
            packetsToProcess.addAll(receivedPacketQueue)
        }
        receivedPacketQueue.clear()
        trackedPosition = null
    }

    private data class StoredPacket(val packet: Packet<ClientGamePacketListener>, val timer: TimerDelay)

    enum class Timing {
        BULK, SINGLE
    }
}