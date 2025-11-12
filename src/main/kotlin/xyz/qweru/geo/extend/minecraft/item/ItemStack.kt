package xyz.qweru.geo.extend.minecraft.item

import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

fun ItemStack.isOf(item: Item): Boolean = this.`is`(item)