package xyz.qweru.geo.extend.kotlin.array

import net.minecraft.world.entity.Entity

fun FloatArray.copyRotationFrom(entity: Entity) {
    this[0] = entity.yRot
    this[1] = entity.xRot
}

fun FloatArray.applyRotation(entity: Entity) {
    entity.yRot = this[0]
    entity.xRot = this[1]
}

fun FloatArray.copy2(array: FloatArray) {
    this[0] = array[0]
    this[1] = array[1]
}