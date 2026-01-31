package xyz.qweru.geo.client.module.misc

import net.minecraft.network.protocol.game.ServerboundContainerClickPacket
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket
import xyz.qweru.geo.client.event.PacketSendEvent
import xyz.qweru.geo.client.helper.network.ChatHelper
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module

class ModulePacketLogger : Module("PacketLogger", "Log certain packets", Category.MISC) {
    val sg = settings.group("Actions")
    val inv by sg.boolean("Inventory", "Inventory clicks and such", false)

    @Handler
    private fun packetSend(e: PacketSendEvent) {
        when (val packet = e.packet) {
            is ServerboundPlayerActionPacket -> {
                if (!inv || packet.action != ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND) {
                    return
                }
                ChatHelper.info("Swap item with offhand")
            }
            is ServerboundPlayerCommandPacket -> {
                if (!inv || packet.action != ServerboundPlayerCommandPacket.Action.OPEN_INVENTORY) {
                    return
                }
                ChatHelper.info("Open inventory")
            }
            is ServerboundContainerClickPacket -> {
                if (!inv) return
                ChatHelper.info("Container click")
                ChatHelper.info("id: ${packet.containerId}, state: ${packet.stateId}")
                ChatHelper.info("slot: ${packet.slotNum}, button: ${packet.buttonNum}")
                ChatHelper.info("type: ${packet.clickType}")
            }
        }
    }
}