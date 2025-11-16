package xyz.qweru.geo.extend.minecraft.entity

import net.minecraft.core.Position
import net.minecraft.core.Vec3i
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3

fun Entity.distanceFromEyesSq(vec: Position) = this.eyePosition.distanceToSqr(vec.x(), vec.y(), vec.z())
fun Entity.distanceFromEyesSq(vec: Vec3i) = this.eyePosition.distanceToSqr(Vec3.atLowerCornerOf(vec))

val Entity.attackCharge: Float
    get() = if (this is Player) this.getAttackStrengthScale(.5f) else 1f
var Entity.isOnGround: Boolean
    get() = this.onGround()
    set(value) = this.setOnGround(value)
var Entity.pos: Vec3
    get() = this.position()
    set(v) = this.setPos(v)