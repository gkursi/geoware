package xyz.qweru.geo.client.helper.entity

import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3

data class Target(val player: Player, val visiblePoint: Vec3?)
