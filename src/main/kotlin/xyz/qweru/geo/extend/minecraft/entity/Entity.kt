package xyz.qweru.geo.extend.minecraft.entity

import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import xyz.qweru.geo.abstraction.entity.GEntity

val Entity.attackCharge: Float
    get() = GEntity.getAttackCharge(entity = this)
var Entity.isOnGround: Boolean
    get() = this.onGround()
    set(value) = this.setOnGround(value)
var Entity.pos: Vec3
    get() = this.position()
    set(v) = this.setPos(v)