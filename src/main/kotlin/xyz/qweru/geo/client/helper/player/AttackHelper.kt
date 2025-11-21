package xyz.qweru.geo.client.helper.player

import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.boss.enderdragon.EndCrystal
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.AxeItem
import net.minecraft.world.item.Items
import xyz.qweru.geo.client.helper.player.inventory.InvHelper
import xyz.qweru.geo.core.Core.mc
import xyz.qweru.geo.extend.minecraft.entity.attackCharge
import xyz.qweru.geo.extend.minecraft.entity.groundTicks
import xyz.qweru.geo.extend.minecraft.item.isOf
import xyz.qweru.geo.extend.minecraft.game.thePlayer

object AttackHelper {
    fun canAttack(target: Entity, playerWeaponOnly: Boolean = false, cooldown: Float = 1f): Boolean {
        if (target is EndCrystal) return target.isAlive
        if (InvHelper.isInMainhand { st -> st.item is AxeItem } && target is LivingEntity && target.useItem.isOf(Items.SHIELD))
            return true
        if (!InvHelper.isInMainhand { st -> InvHelper.isSword(st.item) || st.item is AxeItem } && target is Player && playerWeaponOnly)
            return false

        return mc.thePlayer.attackCharge >= cooldown
    }

    fun canCrit(entity: Player = mc.thePlayer, attackCharge: Float = entity.attackCharge): Boolean =
        entity.fallDistance > 0.075f
        && willCrit(entity, attackCharge = attackCharge)

    fun willCrit(entity: Player = mc.thePlayer, groundTicks: Int = 0, attackCharge: Float = entity.attackCharge): Boolean =
        (attackCharge > 0.9f
        && (!entity.onGround() || (entity is LocalPlayer && entity.groundTicks < groundTicks))
        && !entity.onClimbable() && !entity.isInWater
        && !entity.hasEffect(MobEffects.BLINDNESS)
        && !entity.isPassenger)

}
