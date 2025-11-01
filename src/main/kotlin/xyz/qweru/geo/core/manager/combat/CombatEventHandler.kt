package xyz.qweru.geo.core.manager.combat

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.ArrowEntity
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket
import net.minecraft.text.Text
import xyz.qweru.geo.client.event.*
import xyz.qweru.geo.core.Global.mc
import xyz.qweru.geo.core.event.EventBus
import xyz.qweru.geo.core.event.Handler

object CombatEventHandler {

    @Handler
    private fun onAttackEntity(e: AttackEntityEvent) {
        val target = e.entity
        if (target !is PlayerEntity) return
        playerEvent(e.player, target)
    }

    @Handler
    private fun onPacketReceive(e: PacketReceiveEvent) {
        val packet = e.packet
        when (packet) {
            is EntityDamageS2CPacket -> {
                if (packet.sourceDirectId == -1 && packet.entityId == -1) return
                val sourceDirect = mc.world?.getEntityById(packet.sourceDirectId)
                val source = mc.world?.getEntityById(packet.sourceCauseId)
                val entity = mc.world?.getEntityById(packet.entityId)

//                mc.player?.sendMessage(Text.of("Damage from ${if(source is ArrowEntity) "arrow" else if (source is PlayerEntity) source.gameProfile.name else source.toString()} to $entity"), false)
                EntityDamageEvent.sourceEntity = source
                EntityDamageEvent.directSourceEntity = sourceDirect
                EntityDamageEvent.entity = entity
                EntityDamageEvent.source = packet.createDamageSource(mc.world)

                if (source == null || source !is PlayerEntity) return
                if (entity == null || entity !is PlayerEntity) return
                playerEvent(source, entity)
            }
        }
    }

    private fun playerEvent(attacker: PlayerEntity, target: PlayerEntity) {
        if (attacker == mc.player) EventBus.post(set(AttackPlayerEvent, attacker, target))
        else if (target == mc.player) EventBus.post(set(AttackFromPlayerEvent, attacker, target))
        EventBus.post(set(PlayerAttackPlayerEvent, attacker, target))
    }

    private fun <T : PVPCombatEvent> set(event: T, source: PlayerEntity, player: PlayerEntity): T {
        event.source = source
        event.player = player
        return event
    }
}