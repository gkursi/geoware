package xyz.qweru.geo.extend.minecraft.entity

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3

val Entity.attackCharge: Float
    get() = if (this is Player) this.getAttackStrengthScale(.5f) else 1f
var Entity.isOnGround: Boolean
    get() = this.onGround()
    set(value) = this.setOnGround(value)
var Entity.pos: Vec3
    get() = this.position()
    set(v) = this.setPos(v)