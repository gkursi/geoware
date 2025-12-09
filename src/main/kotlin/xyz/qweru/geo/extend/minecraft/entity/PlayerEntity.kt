package xyz.qweru.geo.extend.minecraft.entity

import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.client.player.LocalPlayer
import net.minecraft.util.Mth
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Items
import net.minecraft.world.phys.Vec3
import xyz.qweru.geo.client.helper.network.ProfileHelper
import xyz.qweru.geo.core.Core.mc
import xyz.qweru.geo.client.helper.player.PlayerHelper
import xyz.qweru.geo.client.helper.player.RotationHelper
import xyz.qweru.geo.client.helper.player.inventory.InvHelper
import xyz.qweru.geo.client.helper.world.WorldHelper
import xyz.qweru.geo.extend.minecraft.item.isOf

fun Player.inRange(range: Float) = this.distanceToSqr(mc.player?.eyePosition ?: Vec3.ZERO) <= Mth.square(range)
fun Player.inRange(range: ClosedRange<Float>) = this.distanceToSqr(mc.player).let {
    it >= Mth.square(range.start) && it <= Mth.square(range.endInclusive)
}
fun Player.inFov(fov: Float) = RotationHelper.getAngle(this) <= fov
fun Player.visiblePoint() = WorldHelper.blockCollision(this.level(), mc.player?.eyePosition ?: Vec3.ZERO, this.boundingBox)
fun LocalPlayer.movementYaw(base: Float = 180f): Float {
    val input = input.moveVector
    var yaw = base
    if (input.x < 0) yaw = -base
    if (input.y > 0) yaw += 45f
    else if (input.y < 0) yaw -= 45f
    return Mth.wrapDegrees(yaw)
}

val Player.canGlide: Boolean
    get() = !this.onGround() && this.getItemBySlot(EquipmentSlot.CHEST).`is`(Items.ELYTRA)
val Player.rotation: FloatArray
    get() = floatArrayOf(yRot, xRot)
val Player.relativeMotion
    get() = PlayerHelper.getRelativeVelocity(this)
val Player.playerListEntry: PlayerInfo?
    get() = ProfileHelper.findPlayerListEntry(this)
val Player.blocking: Boolean
    get() = (useItem.isOf(Items.SHIELD) && isBlocking) || InvHelper.isSword(useItem.item)
