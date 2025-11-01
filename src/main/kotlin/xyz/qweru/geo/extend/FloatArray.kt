package xyz.qweru.geo.extend

import net.minecraft.entity.Entity

fun FloatArray.getRotation(entity: Entity) {
    this[0] = entity.yaw
    this[1] = entity.pitch
}

fun FloatArray.setRotation(entity: Entity) {
    entity.yaw = this[0]
    entity.pitch = this[1]
}

fun FloatArray.copy2(array: FloatArray) {
    this[0] = array[0]
    this[1] = array[1]
}