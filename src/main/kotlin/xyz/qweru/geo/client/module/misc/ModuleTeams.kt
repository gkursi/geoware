package xyz.qweru.geo.client.module.misc

import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import xyz.qweru.geo.core.system.impl.module.Category
import xyz.qweru.geo.core.system.impl.module.Module
import xyz.qweru.geo.extend.kotlin.string.stripMinecraftColorCodes
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import xyz.qweru.geo.extend.minecraft.item.getArmorColor

/**
 * Credit to liquidbounce
 */
class ModuleTeams : Module("Teams", "Don't target players in your team", Category.CONFIG) {
    val sg = settings.general
    val color by sg.boolean("Color", "Don't target if your color matches", true)
    val prefix by sg.boolean("Prefix", "Don't target if your prefix matches", true)
    val scoreboard by sg.boolean("Scoreboard", "Don't target if your scoreboard team matches", true)
    val armor by sg.boolean("Armor Color", "Don't target if your armor color matches", true)
    val armorSlots by sg.multiEnum("Armor Slots", "Armor slots to check", EquipmentSlot.HEAD, EquipmentSlot.CHEST)

    fun isExempt(player: Player): Boolean =
        colorMatches(player) || prefixMatches(player) || teamMatches(player) || armorMatches(player)

    fun colorMatches(player: Player): Boolean {
        if (!color) return false
        val self = mc.thePlayer.displayName?.style?.color
        val target = player.displayName?.style?.color

        return self == target
    }

    fun prefixMatches(player: Player): Boolean {
        if (!prefix) return false
        val targetSplit = player.displayName
            ?.string
            ?.stripMinecraftColorCodes()
            ?.split(" ")
        val clientSplit = player.displayName
            ?.string
            ?.stripMinecraftColorCodes()
            ?.split(" ")

        return targetSplit != null && clientSplit != null
                && targetSplit.size > 1 && clientSplit.size > 1
                && targetSplit[0] == clientSplit[0]
    }

    fun teamMatches(player: Player): Boolean =
        scoreboard && player.isAlliedTo(mc.player)

    fun armorMatches(player: Player): Boolean {
        if (!armor) return false

        for (slot in armorSlots.getEnabled()) {
            if (armorPieceMatches(player, slot)) return true
        }

        return false
    }

    private fun armorPieceMatches(player: Player, slot: EquipmentSlot): Boolean {
        val ownStack = mc.thePlayer.getItemBySlot(slot)
        val otherStack = player.getItemBySlot(slot)

        // returns false if the armor is not dyeable (e.g., iron armor)
        // to avoid a false positive from `null == null`
        val ownColor = ownStack.getArmorColor() ?: return false
        val otherColor = otherStack.getArmorColor() ?: return false

        return ownColor == otherColor
    }
}