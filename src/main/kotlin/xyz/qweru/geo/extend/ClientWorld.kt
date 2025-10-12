package xyz.qweru.geo.extend

import net.minecraft.client.world.ClientWorld
import net.minecraft.util.hit.HitResult
import xyz.qweru.geo.helper.world.RaycastHelper

fun ClientWorld.target(range: Double): HitResult? = RaycastHelper.getCrosshairTarget(range = range)