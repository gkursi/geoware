package xyz.qweru.geo.client.helper.entity

import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import xyz.qweru.geo.client.helper.math.RangeHelper
import xyz.qweru.geo.core.Global
import xyz.qweru.geo.core.Global.mc
import xyz.qweru.geo.core.manager.combat.TargetTracker
import xyz.qweru.geo.core.system.Systems
import xyz.qweru.geo.core.system.friend.Friends
import xyz.qweru.geo.extend.inFov
import xyz.qweru.geo.extend.inRange
import xyz.qweru.geo.extend.thePlayer
import xyz.qweru.geo.extend.theWorld

object TargetHelper {
    fun findTarget(range: ClosedRange<Float>, fov: Float, invisible: Boolean = true): PlayerEntity? {
        var bestRange = Double.MAX_VALUE
        var entity: PlayerEntity? = null
        for (player in mc.theWorld.players) {
            if (isFriendly(player) || isDead(player)) continue
            if (!player.inFov(fov) || !player.inRange(range)) continue
            if (!invisible && isInvisible(player)) continue
            val r = mc.thePlayer.squaredDistanceTo(player)
            if (r < bestRange) {
                bestRange = r
                entity = player
            }
        }
        return entity
    }

    fun isFriendly(player: PlayerEntity): Boolean =
        player == mc.thePlayer || Systems.get(Friends::class).isFriend(player) || !TargetTracker.canTarget(player)

    fun isDead(player: PlayerEntity): Boolean = !player.isAlive

    fun isInvisible(player: PlayerEntity): Boolean =
        player.hasStatusEffect(StatusEffects.INVISIBILITY) && !hasArmor(player) && !hasItems(player)

    fun findTarget(range: Float, fov: Float, invisible: Boolean = true): PlayerEntity? =
        findTarget(RangeHelper.rangeOf(0f, range), fov, invisible)

    fun hasArmor(p: PlayerEntity): Boolean =
        !p.getEquippedStack(EquipmentSlot.HEAD).isEmpty || !p.getEquippedStack(EquipmentSlot.CHEST).isEmpty
        || !p.getEquippedStack(EquipmentSlot.BODY).isEmpty || !p.getEquippedStack(EquipmentSlot.LEGS).isEmpty

    fun hasItems(p: PlayerEntity): Boolean = !p.mainHandStack.isEmpty || !p.offHandStack.isEmpty
}