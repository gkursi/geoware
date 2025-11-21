package xyz.qweru.geo.extend.minecraft.item

import net.minecraft.tags.ItemTags
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.DyedItemColor

fun ItemStack.isOf(item: Item): Boolean = this.`is`(item)
fun ItemStack.getArmorColor(): Int? {
    return if (`is`(ItemTags.DYEABLE)) {
        DyedItemColor.getOrDefault(this, DyedItemColor.LEATHER_COLOR) // #FFA06540
    } else {
        null
    }
}


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
