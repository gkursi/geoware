package xyz.qweru.geo.client.helper.entity

import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import xyz.qweru.geo.client.helper.math.RangeHelper
import xyz.qweru.geo.core.Global.mc
import xyz.qweru.geo.core.manager.combat.TargetTracker
import xyz.qweru.geo.core.system.Systems
import xyz.qweru.geo.core.system.friend.Friends
import xyz.qweru.geo.extend.inFov
import xyz.qweru.geo.extend.inRange
import xyz.qweru.geo.extend.thePlayer
import xyz.qweru.geo.extend.theWorld
import xyz.qweru.geo.extend.visiblePoint

object TargetHelper {
    fun findTarget(
        range: ClosedRange<Float>, wallRange: ClosedRange<Float> = RangeHelper.from(0f, 0f),
        fov: Float = 360f, invisible: Boolean = true
    ): Target? {
        var bestRange = Double.MAX_VALUE
        var target: Target? = null
        for (player in mc.theWorld.players) {
            if (isFriendly(player) || isDead(player)) continue
            if (!player.inFov(fov) || !player.inRange(range)) continue
            if (!invisible && isInvisible(player)) continue
            val visiblePoint = player.visiblePoint()
            if (visiblePoint == null && !player.inRange(wallRange)) continue
            val r = mc.thePlayer.squaredDistanceTo(player)
            if (r < bestRange) {
                bestRange = r
                target = Target(player, visiblePoint)
            }
        }
        return target
    }

    fun isFriendly(player: PlayerEntity): Boolean =
        player == mc.thePlayer || Systems.get(Friends::class).isFriend(player) || !TargetTracker.canTarget(player)

    fun isDead(player: PlayerEntity): Boolean = !player.isAlive

    fun isInvisible(player: PlayerEntity): Boolean =
        player.hasStatusEffect(StatusEffects.INVISIBILITY) && !hasArmor(player) && !hasItems(player)

    fun findTarget(range: Float, wallRange: Float, fov: Float, invisible: Boolean = true) =
        findTarget(RangeHelper.from(0f, range), RangeHelper.from(0f, wallRange), fov, invisible)

    fun hasArmor(p: PlayerEntity): Boolean =
        !p.getEquippedStack(EquipmentSlot.HEAD).isEmpty || !p.getEquippedStack(EquipmentSlot.CHEST).isEmpty
        || !p.getEquippedStack(EquipmentSlot.BODY).isEmpty || !p.getEquippedStack(EquipmentSlot.LEGS).isEmpty

    fun hasItems(p: PlayerEntity): Boolean = !p.mainHandStack.isEmpty || !p.offHandStack.isEmpty
}