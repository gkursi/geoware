package xyz.qweru.geo.client.module.combat

import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
import net.minecraft.sound.SoundEvents
import xyz.qweru.geo.client.event.HandleTaskEvent
import xyz.qweru.geo.client.event.PacketReceiveEvent
import xyz.qweru.geo.core.event.EventPriority
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.manager.combat.CombatState
import xyz.qweru.geo.core.manager.combat.TargetTracker
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.getRelativeVelocity
import xyz.qweru.geo.extend.inRange
import xyz.qweru.geo.helper.timing.TimerDelay
import java.util.concurrent.ConcurrentLinkedQueue

class ModuleBacktrack : Module("Backtrack", "Simulates lag to give you extra reach", Category.COMBAT) {
    val sg = settings.group("General")
    val sc = settings.group("Conditions")

    val timing by sg.enum("Timing", "Timing for packets", Timing.BULK)
    val delay by sg.longRange("Delay", "Packet delay", 150L..250L, 0L..1500L)
    val requireTarget by sg.boolean("Require Target", "Requires a target to be active", true)

    val always by sc.boolean("Always", "Always backtrack", false)
    val combo by sc.int("Combo", "Backtrack when you have this combo or more (0 = disabled)", 0, 0, 5)
    val backwards by sc.boolean("Backwards", "Backtrack when the target is moving backwards", false)
    val inRange by sc.boolean("In Reach", "Backtrack when the target is within reach", true)
    val range by sc.float("Range", "Range to backtrack", 2.5f, 0f, 6f)

    private val receivedPacketQueue = ConcurrentLinkedQueue<StoredPacket>()
    private val packetsToProcess = ConcurrentLinkedQueue<StoredPacket>()
    private val bulkDelay = TimerDelay()

    @Handler
    private fun onPacketProcess(e: HandleTaskEvent) {
        when (timing) {
            Timing.BULK -> {
                if (bulkDelay.hasPassed()) {
                    val s = packetsToProcess.size
                    handleAll()
                    logger.info("${packetsToProcess.size - s}")
                }
            }
            Timing.SINGLE -> receivedPacketQueue.removeIf {
                if (it.timer.hasPassed()) {
                    packetsToProcess.add(it)
                    return@removeIf true
                }
                return@removeIf false
            }
        }

        if (!inGame) packetsToProcess.clear()
        while (!packetsToProcess.isEmpty()) {
            val sp = packetsToProcess.poll()
            sp.packet.apply(mc.networkHandler)
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
            is ChatMessageC2SPacket, is GameMessageS2CPacket, is CommandExecutionC2SPacket -> {
                return
            }

            // Flush on teleport or disconnect
            is PlayerPositionLookS2CPacket, is DisconnectS2CPacket -> {
                handleAll()
                return
            }

            // Ignore own hurt sounds
            is PlaySoundS2CPacket -> {
                if (packet.sound.value() == SoundEvents.ENTITY_PLAYER_HURT) {
                    return
                }
            }

            // Flush on own death
            is HealthUpdateS2CPacket -> {
                if (packet.health <= 0) {
                    handleAll()
                    return
                }
            }
        }

        receivedPacketQueue.add(StoredPacket(packet, TimerDelay().also { it.reset(delay) }))
        e.cancelled = true
    }

    private fun shouldBacktrack(): Boolean {
        if (requireTarget && TargetTracker.target == null) return false
        return always || (CombatState.SELF.combo >= combo && combo > 0) || TargetTracker.target?.let { target ->
            target.getRelativeVelocity().x < 0 && backwards || target.inRange(range) && inRange
        } ?: false
    }

    private fun handleAll(sendPackets: Boolean = true) {
        bulkDelay.reset(delay)
        if (sendPackets) {
            packetsToProcess.addAll(receivedPacketQueue)
        }
        receivedPacketQueue.clear()
    }

    private data class StoredPacket(val packet: Packet<ClientPlayPacketListener>, val timer: TimerDelay)

    enum class Timing {
        BULK, SINGLE
    }
}