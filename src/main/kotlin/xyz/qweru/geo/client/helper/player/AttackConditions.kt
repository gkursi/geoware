package xyz.qweru.geo.client.helper.player

import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player
import xyz.qweru.geo.core.Core
import xyz.qweru.geo.extend.minecraft.entity.attackCharge
import xyz.qweru.geo.extend.minecraft.entity.groundTicks
import xyz.qweru.geo.extend.minecraft.game.thePlayer

object AttackConditions {

    fun canCrit(entity: Player = Core.mc.thePlayer, attackCharge: Float = entity.attackCharge): Boolean =
        entity.fallDistance > 0.075f
        && willCrit(entity, attackCharge = attackCharge)

    fun willCrit(entity: Player = Core.mc.thePlayer, groundTicks: Int = 0, attackCharge: Float = entity.attackCharge): Boolean =
        (attackCharge > 0.9f
        && (!entity.onGround() || (entity is LocalPlayer && entity.groundTicks < groundTicks))
        && !entity.onClimbable() && !entity.isInWater
        && !entity.hasEffect(MobEffects.BLINDNESS)
        && !entity.isPassenger)

}