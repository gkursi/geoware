package xyz.qweru.geo.client.event

import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player

abstract class PVPCombatEvent {
    lateinit var source: Player
    lateinit var player: Player
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
    lateinit var player: Player
    lateinit var entity: Entity
}