package xyz.qweru.geo.helper.player

import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.decoration.EndCrystalEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.AxeItem
import net.minecraft.item.Items
import xyz.qweru.geo.core.Glob.mc
import xyz.qweru.geo.extend.thePlayer
import xyz.qweru.geo.mixin.entity.LivingEntityAccessor

object AttackHelper {
    fun canAttack(target: Entity, playerWeaponOnly: Boolean = false, cooldown: Float = 1f): Boolean {
        if (target is EndCrystalEntity) return target.isAlive
        if (InvHelper.isInMainhand { st -> st.item is AxeItem } && target is LivingEntity && target.activeItem.isOf(Items.SHIELD))
            return true
        if (!InvHelper.isInMainhand { st -> InvHelper.isSword(st.item) || st.item is AxeItem } && target is PlayerEntity && playerWeaponOnly)
            return false

        return mc.thePlayer.getAttackCooldownProgress(0.5f) >= cooldown
    }

    fun canCrit(entity: PlayerEntity = mc.thePlayer, cooldown: Float = mc.thePlayer.getAttackCooldownProgress(0.5f)): Boolean =
        cooldown > 0.9f
        && entity.fallDistance > 0.1f
        && willCrit(entity)

    fun willCrit(entity: PlayerEntity = mc.thePlayer, awaitJump: Boolean = false): Boolean =
        (!entity.isOnGround || ((entity as LivingEntityAccessor).geo_getJumpingCooldown() > 0 && awaitJump))
        && !entity.isClimbing && !entity.isTouchingWater && !entity.isSprinting
        && !entity.hasStatusEffect(StatusEffects.BLINDNESS)
        && !entity.hasVehicle()

}
