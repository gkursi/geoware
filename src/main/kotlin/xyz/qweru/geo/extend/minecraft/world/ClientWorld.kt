package xyz.qweru.geo.extend.minecraft.world

import xyz.qweru.geo.client.helper.world.WorldHelper
import xyz.qweru.geo.core.game.rotation.RotationHandler

fun hit(range: Double, rot: FloatArray = RotationHandler.mouseRotation()) =
    WorldHelper.getCrosshairTarget(range = range, rotation = rot)