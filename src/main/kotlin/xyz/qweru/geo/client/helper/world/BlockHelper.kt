package xyz.qweru.geo.client.helper.world

import net.minecraft.core.BlockPos
import net.minecraft.tags.FluidTags
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.state.BlockState
import xyz.qweru.geo.client.helper.inventory.InvHelper
import xyz.qweru.geo.core.Core.mc
import xyz.qweru.geo.extend.minecraft.game.theLevel
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import xyz.qweru.geo.extend.minecraft.item.getEnchantmentLevel

object BlockHelper {
    fun getBreakDelta(state: BlockState, slot: Int = InvHelper.serverSlot): Double {
        val hardness: Float = state.getDestroySpeed(mc.theLevel, BlockPos.ZERO)
        return if (hardness == -1f) 0.0
               else {
                   getBlockBreakingSpeed(slot, state) / hardness /
                        if (!state.requiresCorrectToolForDrops() || mc.thePlayer.inventory.getItem(slot)
                                .isCorrectToolForDrops(state))
                            30 else 100
               }
    }

    private fun getBlockBreakingSpeed(slot: Int, block: BlockState): Double {
        val tool: ItemStack = mc.thePlayer.inventory.getItem(slot)
        var speed = tool.getDestroySpeed(block).toDouble()

        if (speed > 1) {
            val efficiency: Int = tool.getEnchantmentLevel(Enchantments.EFFICIENCY)
            if (efficiency > 0 && !tool.isEmpty) speed += (efficiency * efficiency + 1).toDouble()
        }

        if (mc.thePlayer.hasEffect(MobEffects.HASTE)) {
            speed *= 1 + ((mc.thePlayer.getEffect(MobEffects.HASTE)?.amplifier ?: 0) + 1) * 0.2f
        }

        if (mc.thePlayer.hasEffect(MobEffects.MINING_FATIGUE)) {
            val k = when (mc.thePlayer.getEffect(MobEffects.MINING_FATIGUE)?.amplifier) {
                0 -> 0.3f
                1 -> 0.09f
                2 -> 0.0027f
                else -> 8.1E-4f
            }

            speed *= k.toDouble()
        }

        if (mc.thePlayer.isEyeInFluid(FluidTags.WATER)) {
            speed *= mc.thePlayer.getAttributeValue(Attributes.SUBMERGED_MINING_SPEED)
        }

        if (!mc.thePlayer.onGround()) {
            speed /= 5.0
        }

        return speed
    }
}