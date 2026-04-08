package xyz.qweru.geo.client.module.misc

import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.common.ServerboundKeepAlivePacket
import net.minecraft.network.protocol.game.*
import xyz.qweru.geo.client.event.PacketQueueEvent
import xyz.qweru.geo.client.event.PostTickEvent
import xyz.qweru.geo.client.helper.network.ChatHelper
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.impl.module.Category
import xyz.qweru.geo.core.system.impl.module.Module

class ModulePacketLogger : Module("PacketLogger", "Log certain packets", Category.MISC) {
    val sg = settings.general
    val dumpOrder by sg.boolean("Dump Order", "Logs the entire packet order of the tick in which the selected packets were sent", true)

    val sa = settings.group("Actions")
    val logInventory by sa.boolean("Inventory", "Inventory clicks and such", false)
    val logEntityInteract by sa.boolean("Entity Interact", "Entity interaction packet", false)

    private val packetOrder = arrayListOf<Packet<ServerPacketListener>>()
    private var packetOrderFlag = false

    @Handler
    private fun packetSend(e: PacketQueueEvent) {
        val packet = e.packet

        if (packet !is ServerboundKeepAlivePacket && packet !is ServerboundChunkBatchReceivedPacket) {
            packetOrder.add(e.packet)
        }

        when (packet) {
            is ServerboundPlayerActionPacket -> {
                if (!logInventory || packet.action != ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND) {
                    return
                }

                ChatHelper.info("Swap item with offhand")
            }

            is ServerboundPlayerCommandPacket -> {
                if (!logInventory || packet.action != ServerboundPlayerCommandPacket.Action.OPEN_INVENTORY) {
                    return
                }

                ChatHelper.info("Open inventory")
            }

            is ServerboundContainerClickPacket -> {
                if (!logInventory) {
                    return
                }

                ChatHelper.info("Container click")
                ChatHelper.info("id: ${packet.containerId}, state: ${packet.stateId}")
                ChatHelper.info("slot: ${packet.slotNum}, button: ${packet.buttonNum}")
                ChatHelper.info("type: ${packet.clickType}")
            }

            is ServerboundInteractPacket -> {
                if (!logEntityInteract) {
                    return
                }

                ChatHelper.info("Entity interact")
            }

            else -> {
                return
            }
        }

        packetOrderFlag = true // only reachable if a packet was flagged
    }

    @Handler
    private fun postTickEvent(e: PostTickEvent) {
        if (!inGame || !packetOrderFlag || !dumpOrder) {
            packetOrder.clear()
            return
        }

        val order = packetOrder.joinToString(" ⟶ ") { packet -> packet.type().id.path }
        ChatHelper.info("Packet order: $order")

        packetOrder.clear()
        packetOrderFlag = false
    }
}