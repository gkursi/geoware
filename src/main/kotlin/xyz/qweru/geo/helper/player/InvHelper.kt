package xyz.qweru.geo.helper.player

import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import xyz.qweru.geo.client.event.PostTickEvent
import xyz.qweru.geo.client.module.config.ModuleSwap
import xyz.qweru.geo.core.Glob.mc
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.module.Modules
import xyz.qweru.geo.core.system.Systems
import xyz.qweru.geo.extend.thePlayer
import java.util.function.Predicate

object InvHelper {

    private var lastSwapPriority = -1
    private var module: ModuleSwap? = null
        get() {
            if (field == null) {
                field = Systems.get(Modules::class).get(ModuleSwap::class)
            }
            return field
        }

    var selectedSlot: Int
        get() = inventory.selectedSlot
        set(value) {
            inventory.selectedSlot = value
        }

    val inventory: PlayerInventory
        get() = mc.thePlayer.getInventory()

    @Handler
    private fun postTick(e: PostTickEvent) {
        lastSwapPriority = -1
        module = Systems.get(Modules::class).get(ModuleSwap::class)
    }

    fun isHolding(item: Predicate<ItemStack>): Boolean = isInMainhand(item) || isInOffhand(item)

    fun isHolding(item: Item): Boolean = isHolding { it.isOf(item) }

    fun isInMainhand(item: Predicate<ItemStack>): Boolean = item.test(getMainhand())

    fun isInMainhand(item: Item): Boolean = isInMainhand { it.isOf(item) }

    fun isInOffhand(item: Predicate<ItemStack>): Boolean = item.test(getOffhand())

    fun isInOffhand(item: Item): Boolean = isInOffhand { it.isOf(item) }

    fun getMainhand(): ItemStack = inventory.getStack(selectedSlot)

    fun getOffhand(): ItemStack = mc.thePlayer.getEquippedStack(EquipmentSlot.OFFHAND)

    fun isSword(item: Item): Boolean =
        item == Items.WOODEN_SWORD || item == Items.STONE_SWORD || item == Items.IRON_SWORD || item == Items.DIAMOND_SWORD || item == Items.NETHERITE_SWORD

    fun swap(item: Item, priority: Int = 0): Boolean = swap({ it.isOf(item) }, priority)

    fun swap(item: Predicate<ItemStack>, priority: Int = 0): Boolean {
        val res = find(item)
        return res.found().also { if (it) res.swap(priority) }
    }

    fun swap(slot: Int, priority: Int = 0) {
        if (slot == selectedSlot) return
        if (module!!.scrollSwap && slot >= module!!.scrollSwapMin - 1) swap0(scrollSlot(slot), priority)
        else swap0(slot, priority)
    }

    fun find(item: Predicate<ItemStack>, start: Int = 0, end: Int = 9): FindResult {
        for (i in start..end - 1) {
            if (item.test(inventory.getStack(i))) {
                return FindResult(i)
            }
        }
        return FindResult.NONE
    }

    private fun swap0(slot: Int, priority: Int) {
        if (priority <= lastSwapPriority) return
        selectedSlot = slot
    }

    private fun scrollSlot(target: Int): Int {
        val current = inventory.selectedSlot
        val rightDist = (target - current + 9) % 9
        val leftDist = (current - target + 9) % 9

        return when {
            rightDist <= leftDist -> (current + 1) % 9 // go right
            else -> (current + 9 - 1) % 9            // go left
        }
    }

    data class FindResult(val slot: Int) {
        companion object {
            val NONE = FindResult(-1)
        }

        fun found(): Boolean = this != NONE

        fun swap(priority: Int = 0) {
            if (found()) swap(slot, priority)
            else throw IllegalStateException("Item not found")
        }

        fun toId(): Int = SlotHelper.indexToId(slot)
    }
}