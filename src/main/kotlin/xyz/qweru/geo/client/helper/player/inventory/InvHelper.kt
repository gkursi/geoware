package xyz.qweru.geo.client.helper.player.inventory

import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import xyz.qweru.geo.client.event.PostTickEvent
import xyz.qweru.geo.client.helper.player.SlotHelper
import xyz.qweru.geo.client.module.config.ModuleSwap
import xyz.qweru.geo.core.Global
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.Systems
import xyz.qweru.geo.core.system.module.Modules
import xyz.qweru.geo.extend.minecraft.game.thePlayer
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

    val inventory: Inventory
        get() = Global.mc.thePlayer.getInventory()

    @Handler
    private fun postTick(e: PostTickEvent) {
        lastSwapPriority = -1
        module = Systems.get(Modules::class).get(ModuleSwap::class)
    }

    fun isHolding(item: (ItemStack) -> Boolean): Boolean = isInMainhand(item) || isInOffhand(item)

    fun isHolding(item: Item): Boolean = isHolding { it.`is`(item) }

    fun isInMainhand(item: (ItemStack) -> Boolean): Boolean = item.invoke(getMainhand())

    fun isInMainhand(item: Item): Boolean = isInMainhand { it.`is`(item) }

    fun isInOffhand(item: (ItemStack) -> Boolean): Boolean = item.invoke(getOffhand())

    fun isInOffhand(item: Item): Boolean = isInOffhand { it.`is`(item) }

    fun getMainhand(): ItemStack = inventory.getItem(selectedSlot)

    fun getOffhand(): ItemStack = Global.mc.thePlayer.getItemBySlot(EquipmentSlot.OFFHAND)

    fun isSword(item: Item): Boolean =
        item == Items.WOODEN_SWORD || item == Items.STONE_SWORD || item == Items.IRON_SWORD || item == Items.DIAMOND_SWORD || item == Items.NETHERITE_SWORD

    fun swap(item: Item, priority: Int = 0): Boolean = swap({ it.`is`(item) }, priority)

    fun swap(item: (ItemStack) -> Boolean, priority: Int = 0): Boolean {
        val res = find(item)
        return res.found().also { if (it) res.swap(priority) }
    }

    fun swap(slot: Int, priority: Int = 0) {
        if (slot == selectedSlot) return
        if (module!!.scrollSwap && slot >= module!!.scrollSwapMin - 1) swap0(scrollSlot(slot), priority)
        else swap0(slot, priority)
    }

    fun find(item: (ItemStack) -> Boolean, start: Int = 0, end: Int = 9): FindResult {
        for (i in start..end - 1) {
            if (item.invoke(inventory.getItem(i))) {
                return FindResult(i)
            }
        }
        return FindResult.NONE
    }

    fun move(): InvAction = InvAction(InvAction.Type.MOVE)

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