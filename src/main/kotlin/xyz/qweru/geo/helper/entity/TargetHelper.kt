package xyz.qweru.geo.helper.entity

import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import xyz.qweru.geo.client.event.PostTickEvent
import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.core.Glob.mc
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.friend.Friends
import xyz.qweru.geo.core.system.Systems
import xyz.qweru.geo.extend.thePlayer
import xyz.qweru.geo.extend.theWorld
import xyz.qweru.geo.helper.player.RotationHelper

object TargetHelper {
    fun findTarget(range: Float, fov: Int, invisible: Boolean = true): PlayerEntity? {
        var bestRange = Double.MAX_VALUE
        val theRange = range * range
        var entity: PlayerEntity? = null
        for (player in mc.theWorld.players) {
            if (player == mc.thePlayer || Systems.get(Friends::class).isFriend(player)) continue
            if (!player.isAlive || player.health <= 0f || RotationHelper.getAngle(player) > fov) continue
            if (!invisible && player.hasStatusEffect(StatusEffects.INVISIBILITY) && !hasArmor(player) && !hasItems(player)) continue
            val r = mc.thePlayer.squaredDistanceTo(player)
            if (r <= theRange && r < bestRange) {
                bestRange = r
                entity = player
            }
        }
        return entity
    }

    fun hasArmor(p: PlayerEntity): Boolean =
        !p.getEquippedStack(EquipmentSlot.HEAD).isEmpty || !p.getEquippedStack(EquipmentSlot.CHEST).isEmpty
        || !p.getEquippedStack(EquipmentSlot.BODY).isEmpty || !p.getEquippedStack(EquipmentSlot.LEGS).isEmpty

    fun hasItems(p: PlayerEntity): Boolean = !p.mainHandStack.isEmpty || !p.offHandStack.isEmpty
}