package xyz.qweru.geo.client.helper.entity

import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.HumanoidArm
import net.minecraft.world.entity.LivingEntity

object EntityHelper {
    fun getArm(entity: LivingEntity, hand: InteractionHand): HumanoidArm =
        when (hand) {
            InteractionHand.MAIN_HAND -> entity.mainArm
            InteractionHand.OFF_HAND -> entity.mainArm.opposite
        }

    fun getHand(entity: LivingEntity, arm: HumanoidArm): InteractionHand =
        if (arm == entity.mainArm) InteractionHand.MAIN_HAND
        else InteractionHand.OFF_HAND
}