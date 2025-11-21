package xyz.qweru.geo.core.tracking

import net.minecraft.world.entity.player.Player

interface PlayerTracker {
    fun isTracking(player: Player): Boolean
}