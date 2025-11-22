package xyz.qweru.geo.extend.minecraft.world

import net.minecraft.client.multiplayer.ClientLevel
import xyz.qweru.geo.client.helper.world.WorldHelper
import xyz.qweru.geo.core.game.rotation.RotationHandler

fun ClientLevel.hit(range: Double, rot: FloatArray = RotationHandler.mouseRotation()) =
    WorldHelper.getCrosshairTarget(range = range, rotation = rot)