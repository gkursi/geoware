package xyz.qweru.geo.extend

import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.util.math.MathHelper
import xyz.qweru.geo.core.Global.mc
import xyz.qweru.geo.client.helper.player.PlayerHelper
import xyz.qweru.geo.client.helper.player.RotationHelper
import xyz.qweru.geo.core.Global

fun PlayerEntity.getRelativeVelocity() = PlayerHelper.getRelativeVelocity(this)
fun PlayerEntity.inRange(range: Float) = this.squaredDistanceTo(mc.player) <= MathHelper.square(range)
fun PlayerEntity.inRange(range: ClosedRange<Float>) = this.squaredDistanceTo(mc.player).let {
    it >= MathHelper.square(range.start) && it <= MathHelper.square(range.endInclusive)
}
fun PlayerEntity.inFov(fov: Float) = RotationHelper.getAngle(this) <= fov
val PlayerEntity.canGlide: Boolean
    get() = !this.isOnGround && this.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA)

val PlayerEntity.rotation: FloatArray
    get() = floatArrayOf(yaw, pitch)