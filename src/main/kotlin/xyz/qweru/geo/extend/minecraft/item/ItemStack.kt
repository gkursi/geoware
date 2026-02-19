package xyz.qweru.geo.extend.minecraft.item

import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponents
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.ItemTags
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.DyedItemColor
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.Enchantments
import xyz.qweru.geo.client.helper.inventory.ItemHelper

fun ItemStack.isOf(item: Item): Boolean = this.`is`(item)

fun ItemStack.getArmorColor(): Int? {
    return if (`is`(ItemTags.DYEABLE)) {
        DyedItemColor.getOrDefault(this, DyedItemColor.LEATHER_COLOR) // #FFA06540
    } else {
        null
    }
}

fun ItemStack.getEnchantmentLevel(enchantment: ResourceKey<Enchantment>) =
    ItemHelper.getEnchantmentLevel(this, enchantment)

fun ItemStack.getAttribute(attribute: Holder<Attribute>) =
    item.components()
        .get(DataComponents.ATTRIBUTE_MODIFIERS)
        ?.modifiers?.find { it.attribute() == attribute }
        ?.modifier()
        ?.amount

val ItemStack.isFootArmor
    get() = this.`is`(ItemTags.FOOT_ARMOR)
val ItemStack.isLegArmor
    get() = this.`is`(ItemTags.LEG_ARMOR)
val ItemStack.isChestArmor
    get() = this.`is`(ItemTags.CHEST_ARMOR)
val ItemStack.isHeadArmor
    get() = this.`is`(ItemTags.HEAD_ARMOR)
val ItemStack.isPlayerArmor
    get() = isFootArmor || isLegArmor || isChestArmor || isHeadArmor

val ItemStack.attackDamage: Double
    get() {
        var damage = getAttribute(Attributes.ATTACK_DAMAGE) ?: 0.0

        val sharpness = getEnchantmentLevel(Enchantments.SHARPNESS)
        if (sharpness > 0) {
            damage += 1 + 0.5 * (sharpness - 1);
        }

        return damage
    }

val ItemStack.armorToughness: Double
    get() {
        var armor = getAttribute(Attributes.ARMOR) ?: 0.0

        // Todo: other enchants
        armor += getEnchantmentLevel(Enchantments.PROTECTION) * .2
        armor *= (getAttribute(Attributes.ARMOR_TOUGHNESS) ?: 0.0) + 8

        return armor
    }