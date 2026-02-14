package xyz.qweru.geo.client.module.world

import net.minecraft.client.gui.screens.inventory.ContainerScreen
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
import xyz.qweru.geo.extend.minecraft.item.armorToughness
import xyz.qweru.geo.extend.minecraft.item.attackDamage
import xyz.qweru.geo.extend.minecraft.item.isOf
import xyz.qweru.geo.extend.minecraft.item.isPlayerArmor

class ModuleChestStealer : Module("ChestStealer", "Automatically loot chests", Category.WORLD) {
    private val sSelect = settings.group("Selection")
    private val sClick = settings.group("Click")

    private val all by sSelect.boolean("All", "Steal everything", false)
    private val items by sSelect.multiEnum("Items", "Items to steal", Target.WEAPON, Target.ARMOR, enumConstants = Target.values())
        .visible { !all }
    private val onlyBetter by sSelect.boolean("Only Better", "Only steal better items than you already have", true)

    private val clickMode by sClick.enum("Click", "Interaction mode", Interact.SHIFT_CLICK)
    private val allowedClicks by sClick.int("Click Count", "Clicks per tick", 1, 1, 12)
    private val clickDelay by sClick.longRange("Click Delay", "Delay between batches of clicks", 50L..65L, 0L..300L)

    private val clickTimer = TimerDelay()

    @Handler
    private fun preSendMove(e: PreMoveSendEvent) {
        if (!clickTimer.hasPassed() || mc.screen !is ContainerScreen) return
        clickTimer.reset(clickDelay)
        repeat(allowedClicks) { stealItem() }
    }

    private fun stealItem() {
        val screen = mc.screen as ContainerScreen
        val item = InvHelper.findInScreen(screen) { stack, _ ->
            !stack.isEmpty && (all
                || items.getEnabled().any {
                    it.canSteal(stack) && !(onlyBetter && !it.isBetter(stack))
                })
        }

        if (!item.found()) return

        ChatHelper.info("Found item: ${item.slot}")
        clickMode.click(item.slot)
    }

    enum class Interact {
        SHIFT_CLICK {
            override fun click(slot: Int) {
                InvHelper.quickMove()
                    .fromId(slot)
                    .apply()
            }
        },
        CLICK {
            override fun click(slot: Int) {
                InvHelper.move()
                    .fromId(slot)
                    .toFreeSlot()
                    ?.apply()
            }
        };

        abstract fun click(slot: Int)
    }

    enum class Target {
        ARMOR {
            override fun canSteal(stack: ItemStack) =
                stack.isPlayerArmor

            override fun isBetter(stack: ItemStack): Boolean =
                (InvHelper.findHighestProtection(stack)
                            ?.armorToughness
                            ?: 0.0) < stack.armorToughness
        },
        BLOCK {
            override fun canSteal(stack: ItemStack): Boolean =
                stack.item is BlockItem

            override fun isBetter(stack: ItemStack): Boolean =
                true
        },
        GAPPLE {
            override fun canSteal(stack: ItemStack): Boolean =
                stack.isOf(Items.GOLDEN_APPLE) || stack.isOf(Items.ENCHANTED_GOLDEN_APPLE)

            override fun isBetter(stack: ItemStack): Boolean =
                true
        },
        WEAPON {
            override fun canSteal(stack: ItemStack): Boolean =
                stack.item is AxeItem || InvHelper.isSword(stack.item) || stack.isOf(Items.MACE)

            override fun isBetter(stack: ItemStack): Boolean =
                (InvHelper.findHighestDamage()?.attackDamage ?: 0.0) < stack.attackDamage
        },
        MCC_ISLAND {
            override fun canSteal(stack: ItemStack): Boolean =
                stack.isOf(Items.MAP)

            override fun isBetter(stack: ItemStack): Boolean =
                true
        };

        abstract fun canSteal(stack: ItemStack): Boolean
        abstract fun isBetter(stack: ItemStack): Boolean
    }
}