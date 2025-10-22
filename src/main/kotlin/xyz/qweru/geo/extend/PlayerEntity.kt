package xyz.qweru.geo.extend

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.MathHelper
import xyz.qweru.geo.core.Global.mc
import xyz.qweru.geo.client.helper.player.PlayerHelper
import xyz.qweru.geo.client.helper.player.RotationHelper

fun PlayerEntity.getRelativeVelocity() = PlayerHelper.getRelativeVelocity(this)
fun PlayerEntity.inRange(range: Float) = this.squaredDistanceTo(mc.player) <= MathHelper.square(range)
fun PlayerEntity.inFov(fov: Float) = RotationHelper.getAngle(this) <= fov