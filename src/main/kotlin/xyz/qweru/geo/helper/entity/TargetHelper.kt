package xyz.qweru.geo.helper.entity

import net.minecraft.entity.player.PlayerEntity
import xyz.qweru.geo.core.Glob.mc
import xyz.qweru.geo.extend.thePlayer
import xyz.qweru.geo.extend.theWorld
import xyz.qweru.geo.helper.player.RotationHelper

object TargetHelper {
    fun findTarget(range: Float, fov: Int): PlayerEntity? {
        var bestRange = Double.MAX_VALUE
        val theRange = range * range
        var entity: PlayerEntity? = null
        for (player in mc.theWorld.players) {
            if (player == mc.thePlayer || RotationHelper.getAngle(player) > fov) continue
            val r = mc.thePlayer.squaredDistanceTo(player)
            if (r <= theRange && r < bestRange) {
                bestRange = r
                entity = player
            }
        }
        return entity
    }
}