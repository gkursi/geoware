package xyz.qweru.geo.client.module.misc

import net.minecraft.world.entity.player.Player
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.kotlin.string.stripMinecraftColorCodes
import xyz.qweru.geo.extend.minecraft.game.thePlayer

/**
 * Partial credit to liquidbounce
 */
class ModuleTeams : Module("Teams", "Don't target players in your team", Category.MISC) {
    val sg = settings.group("General")
    val color by sg.boolean("Color", "Don't target if your color matches", true)
    val prefix by sg.boolean("Prefix", "Don't target if your prefix matches", true)

    fun isExempt(player: Player): Boolean = colorMatches(player) || prefixMatches(player)

    fun colorMatches(player: Player): Boolean {
        if (!color) return false
        val self = mc.thePlayer.displayName?.style?.color
        val target = player.displayName?.style?.color

        return target != null && self?.equals(target) ?: false
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
}