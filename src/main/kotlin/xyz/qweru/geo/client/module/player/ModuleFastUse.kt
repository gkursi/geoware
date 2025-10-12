package xyz.qweru.geo.client.module.player

import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.consume.UseAction
import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.helper.player.InvHelper
import xyz.qweru.geo.helper.timing.TimerDelay
import xyz.qweru.geo.mixin.game.MinecraftClientAccessor

class ModuleFastUse : Module("FastUse", "Reduce item use cooldown", Category.PLAYER) {
    private val sGeneral = settings.group("General")
    private val sTargets = settings.group("Target")

    private val delay by sGeneral.longRange("Delay", "Use delay", 35L..85L, 0L..500L)

    private var crystals by sTargets.boolean("Crystals", "Fast-use crystals", true)
    private var blocks by sTargets.boolean("Blocks", "Fast-use blocks", true)
    private var exp by sTargets.boolean("XP", "Fast-use experience bottles", true)

    private val timer = TimerDelay()

    @Handler
    private fun onTick(e: PreTickEvent) {
        if (inGame && isHoldingItem() && timer.hasPassed()) {
            (mc as MinecraftClientAccessor).geo_setItemUseCooldown(0)
            timer.reset(delay)
        }
    }

    private fun isHoldingItem(): Boolean {
        val mainStack = InvHelper.getMainhand()
        val offStack = InvHelper.getOffhand()
        return isValidStack(mainStack) || (mainStack.useAction == UseAction.NONE && isValidStack(offStack))
    }

    private fun isValidStack(stack: ItemStack): Boolean
        = (crystals && stack.isOf(Items.END_CRYSTAL)) || (blocks && stack.item is BlockItem) || (exp && stack.isOf(Items.EXPERIENCE_BOTTLE))
}