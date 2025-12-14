package xyz.qweru.geo.client.helper.inventory

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap
import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.Object2IntMaps
import net.minecraft.core.Holder
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantment

object ItemHelper {
    fun getEnchantments(itemStack: ItemStack, enchantments: Object2IntMap<Holder<Enchantment?>>) {
        enchantments.clear()

        if (itemStack.isEmpty) return
        val itemEnchantments = itemStack.enchantments.entrySet()

        for (entry in itemEnchantments) {
            enchantments.put(entry.key, entry.intValue)
        }
    }

    fun getEnchantmentLevel(itemStack: ItemStack, enchantment: ResourceKey<Enchantment?>): Int {
        if (itemStack.isEmpty) return 0
        val itemEnchantments: Object2IntMap<Holder<Enchantment?>> = Object2IntArrayMap()
        getEnchantments(itemStack, itemEnchantments)
        return getEnchantmentLevel(itemEnchantments, enchantment)
    }

    fun getEnchantmentLevel(itemEnchantments: Object2IntMap<Holder<Enchantment?>>, enchantment: ResourceKey<Enchantment?>): Int {
        for (entry in Object2IntMaps.fastIterable(itemEnchantments)) {
            if (entry.key?.`is`(enchantment) ?: false) return entry.intValue
        }
        return 0
    }

    @SafeVarargs
    fun hasEnchantments(itemStack: ItemStack, vararg enchantments: ResourceKey<Enchantment?>): Boolean {
        if (itemStack.isEmpty()) return false
        val itemEnchantments: Object2IntMap<Holder<Enchantment?>> = Object2IntArrayMap()
        getEnchantments(itemStack, itemEnchantments)

        for (enchantment in enchantments) {
            if (!hasEnchantment(itemEnchantments, enchantment)) return false
        }
        return true
    }

    fun hasEnchantment(itemStack: ItemStack, enchantmentKey: ResourceKey<Enchantment?>): Boolean {
        if (itemStack.isEmpty) return false
        val itemEnchantments: Object2IntMap<Holder<Enchantment?>> = Object2IntArrayMap()
        getEnchantments(itemStack, itemEnchantments)
        return hasEnchantment(itemEnchantments, enchantmentKey)
    }

    private fun hasEnchantment(
        itemEnchantments: Object2IntMap<Holder<Enchantment?>>,
        enchantmentKey: ResourceKey<Enchantment?>
    ): Boolean {
        for (enchantment in itemEnchantments.keys) {
            if (enchantment.`is`(enchantmentKey)) return true
        }
        return false
    }
}