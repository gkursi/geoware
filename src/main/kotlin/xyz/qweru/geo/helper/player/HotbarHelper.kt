package xyz.qweru.geo.helper.player

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import org.lwjgl.glfw.GLFW
import xyz.qweru.geo.core.Glob.mc
import xyz.qweru.geo.extend.thePlayer
import java.util.function.Predicate

object HotbarHelper {
    fun isInMainhand(item: Predicate<ItemStack>): Boolean =
        item.test(getMainhand())

    fun isInOffhand(item: Predicate<ItemStack>): Boolean =
        item.test(getOffhand())

    fun getMainhand(): ItemStack = mc.thePlayer.inventory.getStack(mc.thePlayer.inventory.selectedSlot)

    fun getOffhand(): ItemStack = mc.thePlayer.inventory.getStack(45)

    fun isSword(item: Item): Boolean
        = item == Items.WOODEN_SWORD || item == Items.STONE_SWORD || item == Items.IRON_SWORD || item == Items.DIAMOND_SWORD || item == Items.NETHERITE_SWORD

    fun swap(item: Predicate<ItemStack>): Boolean {
        val res = find(item)
        return res.found().also { if (it) res.swap() }
    }

    fun swap(slot: Int) {
        if (slot == mc.thePlayer.inventory.selectedSlot) return
            // TODO reimplement global config
//        if (Config.SCROLL_SWAP && slot >= Config.SCROLL_SWAP_MIN) swap0(scrollSlot(slot))
        else swap0(slot)
    }

    fun find(item: Predicate<ItemStack>): FindResult {
        val player = mc.thePlayer
        for (i in 0..8) {
            if (item.test(player.inventory.getStack(i))) {
                return FindResult(i)
            }
        }
        return FindResult.NONE
    }

    private fun swap0(slot: Int) {
        // TODO reimplement global config
//        if (Config.KEY_SWAP) {
//            val key = map(slot)
//            API.keyboardHandler.press(key)
//            API.keyboardHandler.release(key)
//        }
        mc.thePlayer.inventory.selectedSlot = slot
    }

    private fun scrollSlot(target: Int): Int {
        val current = mc.thePlayer.inventory.selectedSlot
        val rightDist = (target - current + 9) % 9
        val leftDist  = (current - target + 9) % 9

        return when {
            rightDist <= leftDist -> (current + 1) % 9 // go right
            else -> (current + 9 - 1) % 9            // go left
        }
    }

    fun map(slot: Int): Int {
        return when (slot) {
            0 -> GLFW.GLFW_KEY_1
            1 -> GLFW.GLFW_KEY_2
            2 -> GLFW.GLFW_KEY_3
            3 -> GLFW.GLFW_KEY_4
            4 -> GLFW.GLFW_KEY_5
            5 -> GLFW.GLFW_KEY_6
            6 -> GLFW.GLFW_KEY_7
            7 -> GLFW.GLFW_KEY_8
            8 -> GLFW.GLFW_KEY_9
            else -> throw IllegalArgumentException()
        }
    }

    data class FindResult(val slot: Int) {
        companion object {
            val NONE = FindResult(-1)
        }

        fun found(): Boolean =
            this != NONE

        fun swap() {
            if (found()) swap(slot)
            else throw IllegalStateException("Item not found")
        }
    }
}