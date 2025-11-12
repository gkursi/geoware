package xyz.qweru.geo.client.helper.entity

import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import xyz.qweru.geo.client.helper.math.RangeHelper
import xyz.qweru.geo.core.Global.mc
import xyz.qweru.geo.core.manager.combat.TargetTracker
import xyz.qweru.geo.core.system.Systems
import xyz.qweru.geo.core.system.friend.Friends
import xyz.qweru.geo.extend.minecraft.entity.inFov
import xyz.qweru.geo.extend.minecraft.entity.inRange
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import xyz.qweru.geo.extend.minecraft.game.theLevel
import xyz.qweru.geo.extend.minecraft.entity.visiblePoint

object TargetHelper {
    fun findTarget(
        range: ClosedRange<Float>, wallRange: ClosedRange<Float> = RangeHelper.from(0f, 0f),
        fov: Float = 360f, invisible: Boolean = true
    ): Target? {
        var bestRange = Double.MAX_VALUE
        var target: Target? = null
        for (player in mc.theLevel.players()) {
            if (isFriendly(player) || isDead(player)) continue
            if (!player.inFov(fov) || !player.inRange(range)) continue
            if (!invisible && isInvisible(player)) continue
            val visiblePoint = player.visiblePoint()
            if (visiblePoint == null && !player.inRange(wallRange)) continue
            val r = mc.thePlayer.distanceToSqr(player)
            if (r < bestRange) {
                bestRange = r
                target = Target(player, visiblePoint)
            }
        }
        return target
    }

    fun isFriendly(player: Player): Boolean =
        player == mc.thePlayer || Systems.get(Friends::class).isFriend(player) || !TargetTracker.canTarget(player)

    fun isDead(player: Player): Boolean = !player.isAlive

    fun isInvisible(player: Player): Boolean =
        player.hasEffect(MobEffects.INVISIBILITY) && !hasArmor(player) && !hasItems(player)

    fun findTarget(range: Float, wallRange: Float, fov: Float, invisible: Boolean = true) =
        findTarget(RangeHelper.from(0f, range), RangeHelper.from(0f, wallRange), fov, invisible)

    fun hasArmor(p: Player): Boolean =
        !p.getItemBySlot(EquipmentSlot.HEAD).isEmpty || !p.getItemBySlot(EquipmentSlot.CHEST).isEmpty
        || !p.getItemBySlot(EquipmentSlot.BODY).isEmpty || !p.getItemBySlot(EquipmentSlot.LEGS).isEmpty

    fun hasItems(p: Player): Boolean = !p.mainHandItem.isEmpty || !p.offhandItem.isEmpty
}