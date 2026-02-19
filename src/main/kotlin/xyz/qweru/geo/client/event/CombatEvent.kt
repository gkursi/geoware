package xyz.qweru.geo.client.event

import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player

abstract class CombatEvent {
    lateinit var source: Player
    lateinit var player: Player
}

object AttackPlayerEvent : CombatEvent()
object AttackFromPlayerEvent : CombatEvent()
object PlayerAttackPlayerEvent : CombatEvent()

object EntityDamageEvent {
    var source: DamageSource? = null
    var entity: Entity? = null
    var sourceEntity: Entity? = null
    var directSourceEntity: Entity? = null
}

object AttackEntityEvent {
    lateinit var player: Player
    lateinit var entity: Entity
}