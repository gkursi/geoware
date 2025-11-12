package xyz.qweru.geo.extend.minecraft.world

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

fun BlockState.isOf(block: Block): Boolean =
    this.`is`(block)
