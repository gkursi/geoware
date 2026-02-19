package xyz.qweru.geo.client.helper.player

import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3
import kotlin.math.cos
import kotlin.math.sin

object PlayerHelper {
    /** velocity relative to the players rotation */
    fun getRelativeVelocity(player: Player): Vec3 {
        val vel = player.deltaMovement

        val yaw = Math.toRadians(-player.yRot.toDouble())
        val cosYaw = cos(yaw)
        val sinYaw = sin(yaw)

        // yaw
        val x1 = vel.x * cosYaw - vel.z * sinYaw
        val z1 = vel.x * sinYaw + vel.z * cosYaw

        return Vec3(x1, vel.y, z1)
    }
}