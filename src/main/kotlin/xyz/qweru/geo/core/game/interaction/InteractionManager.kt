package xyz.qweru.geo.core.game.interaction

import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import xyz.qweru.geo.client.event.PacketSendEvent
import xyz.qweru.geo.client.event.PreMoveSendEvent
import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.client.helper.network.PacketHelper
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.helper.manage.ProposalHandler
import xyz.qweru.geo.core.ui.notification.Notifications
import xyz.qweru.geo.extend.minecraft.network.isInteract

object InteractionManager : ProposalHandler<Interaction>() {

    private var canSendPackets = true
    private var extraPackets = 0

    override fun handleProposal() {
        current?.interact()
        super.handleProposal()
    }

    @Handler
    private fun sendInteraction(e: PreMoveSendEvent) {
        handleProposal()
    }

    @Handler
    private fun preTick(e: PreTickEvent) {
        canSendPackets = true
        extraPackets = 0
    }

    @Handler
    private fun sendPacket(e: PacketSendEvent) {
        val packet = e.packet
        if (!packet.isInteract) return
        if (!canSendPackets) {
//            e.cancelled = true
            extraPackets++
            Notifications.warning("Duplicate interact packet (x$extraPackets)")
        }
        canSendPackets = false
    }

    fun builder(type: Type, block: Config.() -> Unit): Interaction {
        block.invoke(Config)
        return object : Interaction {
            var interacted = false
            val hand = Config.hand
            val position = Config.position
            val entity = Config.entity

            override fun interact() {
                when (type) {
                    Type.ATTACK -> PacketHelper.attackAndSwing(entity!!)
                    Type.USE_ITEM -> PacketHelper.useItemAndSwing(hand)
                    Type.INTERACT -> PacketHelper.interactEntityAndSwing(entity!!, hand)
                    Type.INTERACT_AT -> PacketHelper.interactAtEntityAndSwing(entity!!, hand, position)
                }
            }

            override fun isComplete() = interacted
        }
    }

    object Config {
        var hand = InteractionHand.MAIN_HAND
        var position: Vec3 = Vec3.ZERO
        var entity: Entity? = null
    }

    enum class Type {
        ATTACK, INTERACT, INTERACT_AT, USE_ITEM
    }

}