package xyz.qweru.geo.core.game.packet

import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket
import net.minecraft.network.protocol.game.ClientboundSoundPacket
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket
import net.minecraft.sounds.SoundEvents
import xyz.qweru.basalt.EventPriority
import xyz.qweru.geo.client.event.HandleTasksEvent
import xyz.qweru.geo.client.event.PacketReceiveEvent
import xyz.qweru.geo.client.helper.network.PacketHelper
import xyz.qweru.geo.client.helper.timing.TimerDelay
import xyz.qweru.geo.client.module.config.ModulePacket
import xyz.qweru.geo.core.Core.mc
import xyz.qweru.geo.core.event.EventBus
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.event.PacketManagerFlushEvent
import xyz.qweru.geo.core.system.SystemCache
import java.util.concurrent.ConcurrentLinkedQueue

object PacketManager {
    private val config: ModulePacket by SystemCache.getModule()

    private val receivedPacketQueue = ConcurrentLinkedQueue<Packet<ClientGamePacketListener>>()
    private val packetsToProcess = ConcurrentLinkedQueue<Packet<ClientGamePacketListener>>()
    private val flushTimer = TimerDelay()

    private fun flush(send: Boolean = true) {
        EventBus.post(PacketManagerFlushEvent)
        if (send) {
            packetsToProcess.addAll(receivedPacketQueue)
        }
        receivedPacketQueue.clear()
        flushTimer.reset(0)
    }

    fun forceFlush() = flush(mc.player != null)

    fun lag(onlyIfInactive: Boolean = false) {
        if ((config.delay != ModulePacket.Delay.LIMITLESS || onlyIfInactive) && !flushTimer.hasPassed()) return
        flushTimer.reset(config.time)
    }

    @Handler
    private fun onProcess(e: HandleTasksEvent) {
        if (flushTimer.hasPassed()) {
            flush()
        }

        while (!packetsToProcess.isEmpty()) {
            PacketHelper.handlePacket(packetsToProcess.poll())
        }
    }

    @Handler(priority = EventPriority.LAST)
    private fun onPacket(e: PacketReceiveEvent) {
        if (flushTimer.hasPassed()) return
        val packet = e.packet

        // credit to liquidbounce
        when (packet) {
            // Ignore message-related packets
            is ClientboundPlayerChatPacket, is ClientboundSystemChatPacket -> {
                return
            }

            // Flush on teleport or disconnect
            is ClientboundPlayerPositionPacket, is ClientboundDisconnectPacket -> {
                flush()
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
                    flush()
                    return
                }
            }
        }

        receivedPacketQueue.add(packet)
        e.cancelled = true
    }
}