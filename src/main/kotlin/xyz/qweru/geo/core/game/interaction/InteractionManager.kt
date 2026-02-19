package xyz.qweru.geo.core.game.interaction

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket
import net.minecraft.network.protocol.game.ServerboundUseItemPacket
import net.minecraft.world.InteractionHand
import xyz.qweru.geo.client.event.PacketSendEvent
import xyz.qweru.geo.client.helper.network.PacketHelper
import xyz.qweru.geo.client.helper.player.GameOptions
import xyz.qweru.geo.client.helper.version.ViaHelper
import xyz.qweru.geo.core.Core.mc
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.helper.manage.Proposal
import xyz.qweru.geo.core.helper.manage.ProposalHandler
import xyz.qweru.geo.extend.minecraft.game.thePlayer

object InteractionManager : ProposalHandler<InteractionManager.Action>() {

    val useItem = object : State() {
        override fun applyClient() {
            GameOptions.syncBind(GameOptions::useKey)
            if (client) {
                if (mc.thePlayer.isUsingItem) return
                mc.player?.startUsingItem(useHand)
//                GameOptions.useKey = true
            } else {
                if (!mc.thePlayer.isUsingItem) return
                mc.player?.stopUsingItem()
            }
        }

        override fun applyServer() {
            if (server) {
                PacketHelper.useItemAndSwing(useHand)
            } else {
                PacketHelper.sendPacket(
                    ServerboundPlayerActionPacket(
                        ServerboundPlayerActionPacket.Action.RELEASE_USE_ITEM, BlockPos.ZERO, ViaHelper.getReleaseItemDirection()
                    )
                )
            }
        }

    }

    var useHand = InteractionHand.MAIN_HAND

    @Handler
    private fun sendPacket(e: PacketSendEvent) {
        when (val packet = e.packet) {
            is ServerboundUseItemPacket, is ServerboundUseItemOnPacket -> {
                useItem.server = true
            }
            is ServerboundPlayerActionPacket -> {
                if (packet.action == ServerboundPlayerActionPacket.Action.RELEASE_USE_ITEM) {
                    useItem.server = false
                }
            }
        }
    }

    fun releaseUsedItem(priority: Int) = propose(
        proposal = {
            useItem.server = false
            useItem.applyServer()
        },
        priority = priority
    )

    fun useItem(priority: Int, hand: InteractionHand) = propose(
        proposal = {
            useHand = hand
            useItem.server = true
            useItem.applyServer()
        },
        priority = priority
    )

    abstract class State {
        var client: Boolean = false
        var server: Boolean = false

        open fun syncToServer() {
            client = server
            applyClient()
        }

        open fun syncToClient() {
            server = client
            applyServer()
        }

        abstract fun applyClient()
        abstract fun applyServer()
    }

    fun interface Action : (InteractionManager) -> Unit, Proposal
}