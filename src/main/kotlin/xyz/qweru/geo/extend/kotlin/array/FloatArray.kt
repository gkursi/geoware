package xyz.qweru.geo.extend.kotlin.array

import net.minecraft.world.entity.Entity
import xyz.qweru.geo.client.helper.player.RotationHelper
import xyz.qweru.geo.core.game.rotation.data.Rotation
import xyz.qweru.geo.extend.kotlin.math.wrappedDeg

fun FloatArray.copyRotationFrom(entity: Entity) {
    this[0] = entity.yRot
    this[1] = entity.xRot
}

fun FloatArray.copyRotation(rotation: Rotation) {
    this[0] = rotation.yaw
    this[1] = rotation.pitch
}

fun FloatArray.setEntityRotation(entity: Entity) {
    val yaw = RotationHelper.unwrapYaw(this[0].wrappedDeg, entity.yRot)

    entity.yRot = yaw
    entity.xRot = this[1]
}

fun FloatArray.copy2(array: FloatArray) {
    this[0] = array[0]
    this[1] = array[1]
}