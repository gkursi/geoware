package xyz.qweru.geo.extend

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.MathHelper
import xyz.qweru.geo.core.Glob.mc
import xyz.qweru.geo.helper.player.PlayerHelper
import xyz.qweru.geo.helper.player.RotationHelper

fun PlayerEntity.getRelativeVelocity() = PlayerHelper.getRelativeVelocity(this)
fun PlayerEntity.inRange(range: Float) = this.squaredDistanceTo(mc.player) <= MathHelper.square(range)
fun PlayerEntity.inFov(fov: Float) = RotationHelper.getAngle(this) <= fov