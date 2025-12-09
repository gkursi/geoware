package xyz.qweru.geo.extend.minecraft.item

import net.minecraft.resources.ResourceKey
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.DyedItemColor
import net.minecraft.world.item.enchantment.Enchantment
import xyz.qweru.geo.client.helper.player.inventory.ItemHelper

fun ItemStack.isOf(item: Item): Boolean = this.`is`(item)
fun ItemStack.getArmorColor(): Int? {
    return if (`is`(ItemTags.DYEABLE)) {
        DyedItemColor.getOrDefault(this, DyedItemColor.LEATHER_COLOR) // #FFA06540
    } else {
        null
    }
}
fun ItemStack.getEnchantmentLevel(enchantment: ResourceKey<Enchantment?>) =
    ItemHelper.getEnchantmentLevel(this, enchantment)

val ItemStack.isFootArmor
    get() = this.`is`(ItemTags.LEG_ARMOR)
val ItemStack.isLegArmor
    get() = this.`is`(ItemTags.LEG_ARMOR)
val ItemStack.isChestArmor
    get() = this.`is`(ItemTags.CHEST_ARMOR)
val ItemStack.isHeadArmor
    get() = this.`is`(ItemTags.HEAD_ARMOR)
val ItemStack.isPlayerArmor
    get() = isFootArmor || isLegArmor || isChestArmor || isHeadArmor
