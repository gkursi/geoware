package xyz.qweru.geo.extend

import net.minecraft.util.hit.HitResult
import xyz.qweru.geo.client.helper.world.WorldHelper
import xyz.qweru.geo.core.manager.rotation.RotationHandler

fun hit(range: Double, rot: FloatArray = RotationHandler.mouseRotation()): HitResult? = WorldHelper.getCrosshairTarget(range = range, rotation = rot)