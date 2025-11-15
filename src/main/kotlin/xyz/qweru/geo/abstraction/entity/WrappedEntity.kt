package xyz.qweru.geo.abstraction.entity

import net.minecraft.world.entity.Entity
import xyz.qweru.geo.extend.minecraft.entity.attackCharge

class WrappedEntity(private val _entity: Entity) {
    val attackCharge
        get() = _entity.attackCharge

}