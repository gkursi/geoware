package xyz.qweru.geo.core.manager.combat

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket
import xyz.qweru.geo.client.event.*
import xyz.qweru.geo.core.Global.mc
import xyz.qweru.geo.core.event.EventBus
import xyz.qweru.geo.core.event.Handler

object CombatEventHandler {

    @Handler
    private fun onAttackEntity(e: AttackEntityEvent) {
        val target = e.entity
        if (target !is PlayerEntity) return
        post(e.player, target)
    }

    @Handler
    private fun onPacketReceive(e: PacketReceiveEvent) {
        val packet = e.packet
        when (packet) {
            is EntityDamageS2CPacket -> {
                if (packet.sourceDirectId == -1) return
                val source = mc.world?.getEntityById(packet.sourceDirectId)
                if (source == null || source !is PlayerEntity) return
                val target = mc.world?.getEntityById(packet.entityId)
                if (target == null || target !is PlayerEntity) return
                post(source, target)
            }
        }
    }

    private fun post(attacker: PlayerEntity, target: PlayerEntity) {
        if (attacker == mc.player) EventBus.post(set(AttackPlayerEvent, attacker, target))
        else if (target == mc.player) EventBus.post(set(AttackFromPlayerEvent, attacker, target))
        EventBus.post(set(PlayerAttackPlayerEvent, attacker, target))
    }

    private fun <T : CombatEvent> set(event: T, source: PlayerEntity, player: PlayerEntity): T {
        event.source = source
        event.player = player
        return event
    }
}