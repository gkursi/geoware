package xyz.qweru.geo.core.manager.combat

import net.minecraft.network.protocol.game.ClientboundDamageEventPacket
import net.minecraft.world.entity.player.Player
import xyz.qweru.geo.client.event.*
import xyz.qweru.geo.core.Global.mc
import xyz.qweru.geo.core.event.EventBus
import xyz.qweru.geo.core.event.Handler

object CombatEventHandler {

    @Handler
    private fun onAttackEntity(e: AttackEntityEvent) {
        val target = e.entity
        if (target !is Player) return
        playerEvent(e.player, target)
    }

    @Handler
    private fun onPacketReceive(e: PacketReceiveEvent) {
        val packet = e.packet
        when (packet) {
            is ClientboundDamageEventPacket -> {
                if (packet.sourceDirectId == -1 && packet.entityId == -1) return
                val sourceDirect = mc.level?.getEntity(packet.sourceDirectId)
                val source = mc.level?.getEntity(packet.sourceCauseId)
                val entity = mc.level?.getEntity(packet.entityId)

//                mc.player?.sendMessage(Text.of("Damage from ${if(source is ArrowEntity) "arrow" else if (source is Player) source.gameProfile.name else source.toString()} to $entity"), false)
                EntityDamageEvent.sourceEntity = source
                EntityDamageEvent.directSourceEntity = sourceDirect
                EntityDamageEvent.entity = entity
                EntityDamageEvent.source = packet.getSource(mc.level)

                if (source == null || source !is Player) return
                if (entity == null || entity !is Player) return
                playerEvent(source, entity)
            }
        }
    }

    private fun playerEvent(attacker: Player, target: Player) {
        if (attacker == mc.player) EventBus.post(set(AttackPlayerEvent, attacker, target))
        else if (target == mc.player) EventBus.post(set(AttackFromPlayerEvent, attacker, target))
        EventBus.post(set(PlayerAttackPlayerEvent, attacker, target))
    }

    private fun <T : PVPCombatEvent> set(event: T, source: Player, player: Player): T {
        event.source = source
        event.player = player
        return event
    }
}