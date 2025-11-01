package xyz.qweru.geo.client.event

import net.minecraft.entity.Entity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.player.PlayerEntity

abstract class PVPCombatEvent {
    lateinit var source: PlayerEntity
    lateinit var player: PlayerEntity
}

object AttackPlayerEvent : PVPCombatEvent()
object AttackFromPlayerEvent : PVPCombatEvent()
object PlayerAttackPlayerEvent : PVPCombatEvent()

object EntityDamageEvent {
    lateinit var source: DamageSource
    var entity: Entity? = null
    var sourceEntity: Entity? = null
    var directSourceEntity: Entity? = null
}

object AttackEntityEvent {
    lateinit var player: PlayerEntity
    lateinit var entity: Entity
}