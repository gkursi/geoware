package xyz.qweru.geo.extend

import net.minecraft.client.world.ClientWorld
import net.minecraft.util.hit.HitResult
import xyz.qweru.geo.client.helper.world.WorldHelper

fun ClientWorld.target(range: Double): HitResult? = WorldHelper.getCrosshairTarget(range = range)