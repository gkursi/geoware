package xyz.qweru.geo.extend.kotlin.array

import net.minecraft.world.entity.Entity
import xyz.qweru.geo.client.helper.player.RotationHelper
import xyz.qweru.geo.core.game.rotation.Rotation
import xyz.qweru.geo.core.game.rotation.RotationHandler
import xyz.qweru.geo.extend.kotlin.math.wrapped

fun FloatArray.copyRotationFrom(entity: Entity) {
    this[0] = entity.yRot
    this[1] = entity.xRot
}

fun FloatArray.copyRotation(rotation: Rotation) {
    this[0] = rotation.yaw
    this[1] = rotation.pitch
}

fun FloatArray.applyRotation(entity: Entity) {
    val yaw = RotationHelper.unwrapYaw(this[0].wrapped, entity.yRot)

    entity.yRot = yaw
    entity.xRot = this[1]
}

fun FloatArray.copy2(array: FloatArray) {
    this[0] = array[0]
    this[1] = array[1]
}