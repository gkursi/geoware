package xyz.qweru.geo.client.helper.entity

import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import xyz.qweru.geo.client.helper.math.RangeHelper
import xyz.qweru.geo.core.Core.mc
import xyz.qweru.geo.core.tracking.bot.BotTracker
import xyz.qweru.geo.core.game.combat.TargetTracker.config
import xyz.qweru.geo.core.game.combat.TargetTracker.teams
import xyz.qweru.geo.core.system.SystemCache
import xyz.qweru.geo.core.system.impl.friend.Friends
import xyz.qweru.geo.extend.minecraft.entity.inFov
import xyz.qweru.geo.extend.minecraft.entity.inRange
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import xyz.qweru.geo.extend.minecraft.game.theLevel
import xyz.qweru.geo.extend.minecraft.entity.visiblePoint

object TargetHelper {
    val friends: Friends by SystemCache.get()

    fun findTarget(
        range: ClosedRange<Float>, wallRange: ClosedRange<Float> = 0f..0f,
        fov: Float = 360f, invisible: Boolean = true
    ): Target? {
        var bestRange = Double.MAX_VALUE
        var target: Target? = null
        for (player in mc.theLevel.players()) {
            if (isFriendly(player) || !canTarget(player) || isDead(player)) continue
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
        player == mc.thePlayer || (friends.isFriend(player) && config.excludeFriends)

    fun canTarget(player: Player): Boolean =
        (!teams.enabled || !teams.isExempt(player)) && !BotTracker.isTracking(player)

    fun isDead(player: Player): Boolean = !player.isAlive

    fun isInvisible(player: Player): Boolean =
        player.hasEffect(MobEffects.INVISIBILITY) && !hasArmor(player) && !hasItems(player)

    fun findTarget(range: Float, wallRange: Float, fov: Float, invisible: Boolean = true) =
        findTarget(RangeHelper.of(0f, range), RangeHelper.of(0f, wallRange), fov, invisible)

    fun hasArmor(p: Player): Boolean =
        !p.getItemBySlot(EquipmentSlot.HEAD).isEmpty || !p.getItemBySlot(EquipmentSlot.CHEST).isEmpty
        || !p.getItemBySlot(EquipmentSlot.BODY).isEmpty || !p.getItemBySlot(EquipmentSlot.LEGS).isEmpty

    fun hasItems(p: Player): Boolean = !p.mainHandItem.isEmpty || !p.offhandItem.isEmpty
}