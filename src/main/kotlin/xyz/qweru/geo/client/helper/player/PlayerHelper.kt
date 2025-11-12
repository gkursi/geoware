package xyz.qweru.geo.client.helper.player

import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3
import kotlin.math.cos
import kotlin.math.sin

object PlayerHelper {
    /** get the players velocity relative to their rotation */
    fun getRelativeVelocity(player: Player): Vec3 {
        val vel = player.deltaMovement

        val yaw = Math.toRadians(-player.yRot.toDouble())
        val pitch = Math.toRadians(-player.xRot.toDouble())
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

        return Vec3(x1, y2, z2)
    }
}