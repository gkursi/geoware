package xyz.qweru.geo.client.helper.entity

import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.util.Arm
import net.minecraft.util.Hand

object EntityHelper {
    fun getArm(entity: LivingEntity, hand: Hand): Arm =
        when (hand) {
            Hand.MAIN_HAND -> entity.mainArm
            Hand.OFF_HAND -> entity.mainArm.opposite
        }

    fun getHand(entity: LivingEntity, arm: Arm): Hand =
        if (arm == entity.mainArm) Hand.MAIN_HAND
        else Hand.OFF_HAND
}