package xyz.qweru.geo.client.module.combat

import net.minecraft.world.item.AxeItem
import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.client.helper.inventory.InvHelper
import xyz.qweru.geo.client.helper.timing.TimerDelay
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.game.combat.TargetTracker
import xyz.qweru.geo.core.system.impl.module.Category
import xyz.qweru.geo.core.system.impl.module.Module
import xyz.qweru.geo.extend.minecraft.entity.blocking
import xyz.qweru.geo.extend.minecraft.game.thePlayer

class ModuleAxeSwap : Module("AxeSwap", "Automatically swaps to an axe when the target is using a shield", Category.COMBAT) {

    val sg = settings.general
    val delay by sg.longRange("Delay", "Delay", 250L..300L, 0L..500L)
    val swapBack by sg.boolean("Swap Back", "Swaps back to previous slot", true)
    val pauseUse by sg.boolean("Pause On Eat", "Don't swap while using an item", true)

    private val timer = TimerDelay()
    private var slot = -1

    @Handler
    private fun onTick(e: PreTickEvent) {
        if (!inGame || !timer.hasPassed() || (mc.thePlayer.isUsingItem && pauseUse)) return
        if (shouldSwap()) {
            timer.reset(delay)
            slot = InvHelper.selectedSlot
            InvHelper.swap({ it.item is AxeItem })
        } else if (slot != -1 && swapBack && TargetTracker.target?.blocking == false) {
            timer.reset(delay)
            InvHelper.swap(slot)
            slot = -1
        }
    }

    private fun shouldSwap(): Boolean =
         TargetTracker.lookingAtTarget
                 && TargetTracker.target?.blocking == true
                 && !InvHelper.isHolding { it.item is AxeItem }
}