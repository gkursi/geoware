package xyz.qweru.geo.client.event

import net.minecraft.world.phys.Vec3
import xyz.qweru.geo.core.Core.mc
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import xyz.qweru.geo.extend.minecraft.world.withStrafe
import xyz.qweru.geo.mixin.math.Vec3Accessor
import kotlin.math.sqrt

object PreMovementTickEvent
object PostMovementTickEvent {
    var velX = 0.0
        set(value) {
            field = value
            (mc.thePlayer.deltaMovement as Vec3Accessor).geo_setX(value)
        }
    var velY = 0.0
        set(value) {
            field = value
            (mc.thePlayer.deltaMovement as Vec3Accessor).geo_setY(value)
        }
    var velZ = 0.0
        set(value) {
            field = value
            (mc.thePlayer.deltaMovement as Vec3Accessor).geo_setZ(value)
        }

    fun clampHorizontal(maxMag: Double) {
        val magnitude = sqrt(velX * velX + velZ * velZ)
        if (magnitude > maxMag) {
            velX = (velX / magnitude) * maxMag
            velZ = (velZ / magnitude) * maxMag
        }
    }

    fun clamp(maxMag: Double) {
        val magnitude = sqrt(velX * velX + velY * velY + velZ * velZ)
        if (magnitude > maxMag) {
            velX = (velX / magnitude) * maxMag
            velY = (velY / magnitude) * maxMag
            velZ = (velZ / magnitude) * maxMag
        }
    }

    fun addStrafe(speed: Float) {
        val vec = Vec3.ZERO.withStrafe(speed = speed.toDouble())
        velX += vec.x
        velY += vec.y
        velZ += vec.z
    }

    fun setStrafe(speed: Float) {
        val vec = Vec3.ZERO.withStrafe(speed = speed.toDouble())
        velX = vec.x
        velY = vec.y
        velZ = vec.z
    }
}

object TravelEvent {
    lateinit var vec: Vec3

    var velX = 0.0
        set(value) {
            field = value
            (vec as Vec3Accessor).geo_setX(value)
        }
    var velY = 0.0
        set(value) {
            field = value
            (vec as Vec3Accessor).geo_setY(value)
        }
    var velZ = 0.0
        set(value) {
            field = value
            (vec as Vec3Accessor).geo_setZ(value)
        }

    fun clampHorizontal(maxMag: Double) {
        val magnitude = sqrt(velX * velX + velZ * velZ)
        if (magnitude > maxMag) {
            velX = (velX / magnitude) * maxMag
            velZ = (velZ / magnitude) * maxMag
        }
    }

    fun clamp(maxMag: Double) {
        val magnitude = sqrt(velX * velX + velY * velY + velZ * velZ)
        if (magnitude > maxMag) {
            velX = (velX / magnitude) * maxMag
            velY = (velY / magnitude) * maxMag
            velZ = (velZ / magnitude) * maxMag
        }
    }
}

object ForwardMovementCheckEvent {
    var hasForwardMovement = false
}

object PostInputTick