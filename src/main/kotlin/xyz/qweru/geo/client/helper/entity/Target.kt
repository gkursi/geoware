package xyz.qweru.geo.client.helper.entity

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.Vec3d

data class Target(val player: PlayerEntity, val visiblePoint: Vec3d?)
