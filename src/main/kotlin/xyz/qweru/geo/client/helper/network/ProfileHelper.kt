package xyz.qweru.geo.client.helper.network

import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.world.entity.player.Player
import xyz.qweru.geo.core.Core.mc

object ProfileHelper {
    fun findPlayerListEntry(player: Player): PlayerInfo? =
        mc.connection?.getPlayerInfo(player.gameProfile.id)
}