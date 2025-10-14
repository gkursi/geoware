package xyz.qweru.geo.helper.player

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.Vec3d
import kotlin.math.cos
import kotlin.math.sin

object PlayerHelper {
    /** get the players velocity relative to their rotation */
    fun getRelativeVelocity(player: PlayerEntity): Vec3d {
        val vel = player.velocity

        val yaw = Math.toRadians(-player.yaw.toDouble())
        val pitch = Math.toRadians(-player.pitch.toDouble())
        val cosYaw = cos(yaw)
        val sinYaw = sin(yaw)
        val cosPitch = cos(pitch)
        val sinPitch = sin(pitch)

        // yaw
        val x1 = vel.x * cosYaw - vel.z * sinYaw
        val z1 = vel.x * sinYaw + vel.z * cosYaw
        // pitch
        val y2 = vel.y * cosPitch - z1 * sinPitch
        val z2 = vel.y * sinPitch + z1 * cosPitch

        return Vec3d(x1, y2, z2)
    }
}