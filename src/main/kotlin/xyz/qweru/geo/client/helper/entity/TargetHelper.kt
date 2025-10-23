package xyz.qweru.geo.client.helper.entity

import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import xyz.qweru.geo.client.helper.math.RangeHelper
import xyz.qweru.geo.core.Global.mc
import xyz.qweru.geo.core.system.friend.Friends
import xyz.qweru.geo.core.system.Systems
import xyz.qweru.geo.extend.thePlayer
import xyz.qweru.geo.extend.theWorld
import xyz.qweru.geo.client.helper.player.RotationHelper

object TargetHelper {
    fun findTarget(range: ClosedRange<Float>, fov: Float, invisible: Boolean = true): PlayerEntity? {
        var bestRange = Double.MAX_VALUE
        val minRange = range.start * range.start
        val maxRange = range.endInclusive * range.endInclusive
        var entity: PlayerEntity? = null
        for (player in mc.theWorld.players) {
            if (player == mc.thePlayer || Systems.get(Friends::class).isFriend(player)) continue
            if (!player.isAlive || player.health <= 0f || RotationHelper.getAngle(player) > fov) continue
            if (!invisible && player.hasStatusEffect(StatusEffects.INVISIBILITY) && !hasArmor(player) && !hasItems(player)) continue
            val r = mc.thePlayer.squaredDistanceTo(player)
            if (r <= maxRange && r >= minRange && r < bestRange) {
                bestRange = r
                entity = player
            }
        }
        return entity
    }

    fun findTarget(range: Float, fov: Float, invisible: Boolean = true): PlayerEntity? =
        findTarget(RangeHelper.rangeOf(range, range), fov, invisible)

    fun hasArmor(p: PlayerEntity): Boolean =
        !p.getEquippedStack(EquipmentSlot.HEAD).isEmpty || !p.getEquippedStack(EquipmentSlot.CHEST).isEmpty
        || !p.getEquippedStack(EquipmentSlot.BODY).isEmpty || !p.getEquippedStack(EquipmentSlot.LEGS).isEmpty

    fun hasItems(p: PlayerEntity): Boolean = !p.mainHandStack.isEmpty || !p.offHandStack.isEmpty
}