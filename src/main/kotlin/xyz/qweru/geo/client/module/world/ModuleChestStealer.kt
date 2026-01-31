package xyz.qweru.geo.client.module.world

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.AxeItem
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import xyz.qweru.geo.client.event.PreMoveSendEvent
import xyz.qweru.geo.client.helper.inventory.InvHelper
import xyz.qweru.geo.client.helper.network.ChatHelper
import xyz.qweru.geo.client.helper.timing.TimerDelay
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.minecraft.item.isOf
import xyz.qweru.geo.extend.minecraft.item.isPlayerArmor

class ModuleChestStealer : Module("ChestStealer", "Automatically loot chests", Category.WORLD) {
    private val sSelect = settings.group("Selection")
    private val sClick = settings.group("Click")

    private val all by sSelect.boolean("All", "Steal everything", false)
    private val items by sSelect.multiEnum("Items", "Items to steal", Target.WEAPON, Target.ARMOR)

    private val clickMode by sClick.enum("Click", "Interaction mode", Interact.SHIFT_CLICK)
    private val allowedClicks by sClick.int("Click Count", "Clicks per tick (0 = unlimited)", 1, 0, 12)
    private val clickDelay by sClick.longRange("Click Delay", "Delay between batches of clicks", 50L..65L, 0L..300L)

    private val clickTimer = TimerDelay()

    @Handler
    private fun preSendMove(e: PreMoveSendEvent) {
        if (!clickTimer.hasPassed() || mc.screen !is AbstractContainerScreen<*>) return
        clickTimer.reset(clickDelay)
        repeat(allowedClicks) { stealItem() }
    }

    private fun stealItem() {
        val item = InvHelper.find({ stack -> all || items.getEnabled().firstOrNull { it.selector.invoke(stack) } != null })
        if (!item.found()) return
        ChatHelper.info("Found item: ${item.slot}")
        clickMode.action.invoke(item.slot)
    }

    enum class Interact(val action: (Int) -> Unit) {
        CLICK({
            InvHelper.move().from(it).toFreeSlot()?.apply()
        }),
        SHIFT_CLICK({
            InvHelper.quickMove().from(it).apply()
        })
    }

    enum class Target(val selector: (ItemStack) -> Boolean) {
        ARMOR({ it.isPlayerArmor }),
        BLOCK({ it.item is BlockItem }),
        GAPPLE({ it.isOf(Items.GOLDEN_APPLE) || it.isOf(Items.ENCHANTED_GOLDEN_APPLE) }),
        WEAPON({ it.item is AxeItem || InvHelper.isSword(it.item) || it.isOf(Items.MACE) })
    }

}