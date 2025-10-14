package xyz.qweru.geo.extend

import net.minecraft.entity.player.PlayerEntity
import xyz.qweru.geo.helper.player.PlayerHelper

fun PlayerEntity.getRelativeVelocity() = PlayerHelper.getRelativeVelocity(this)