package xyz.qweru.geo.client.module.misc

import net.minecraft.entity.player.PlayerEntity
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.stripMinecraftColorCodes
import xyz.qweru.geo.extend.thePlayer

/**
 * Partial credit to liquidbounce
 */
class ModuleTeams : Module("Teams", "Don't target players in your team", Category.MISC) {
    val sg = settings.group("General")
    val color by sg.boolean("Color", "Don't target if your color matches", true)
    val prefix by sg.boolean("Prefix", "Don't target if your prefix matches", true)

    fun isExempt(player: PlayerEntity): Boolean = colorMatches(player) || prefixMatches(player)

    fun colorMatches(player: PlayerEntity): Boolean {
        if (!color) return false
        val self = mc.thePlayer.displayName?.style?.color
        val target = player.displayName?.style?.color

        return target != null && self?.equals(target) ?: false
    }

    fun prefixMatches(player: PlayerEntity): Boolean {
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