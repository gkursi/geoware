package xyz.qweru.geo.client.event

import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity

abstract class CombatEvent {
    lateinit var source: PlayerEntity
    lateinit var player: PlayerEntity
}

object AttackPlayerEvent : CombatEvent()
object AttackFromPlayerEvent : CombatEvent()
object PlayerAttackPlayerEvent : CombatEvent()

object AttackEntityEvent {
    lateinit var player: PlayerEntity
    lateinit var entity: Entity
}