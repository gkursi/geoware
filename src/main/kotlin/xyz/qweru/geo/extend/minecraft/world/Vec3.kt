package xyz.qweru.geo.extend.minecraft.world

import net.minecraft.world.phys.Vec3
import xyz.qweru.geo.client.helper.player.GameOptions
import xyz.qweru.geo.client.helper.player.RotationHelper
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

val Vec3.sqrtSpeed: Double
    get() = hypot(x, z)

fun Vec3.withStrafe(
    speed: Double = sqrtSpeed,
    strength: Double = 1.0,
    yaw: Float = RotationHelper.inputToYaw(),
): Vec3 {
    if (GameOptions.moving) {
        return Vec3(0.0, y, 0.0)
    }

    val prevX = x * (1.0 - strength)
    val prevZ = z * (1.0 - strength)
    val useSpeed = speed * strength

    val angle = Math.toRadians(yaw.toDouble())
    val x = (-sin(angle) * useSpeed) + prevX
    val z = (cos(angle) * useSpeed) + prevZ
    return Vec3(x, y, z)
}
