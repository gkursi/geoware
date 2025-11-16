package xyz.qweru.geo.extend.minecraft.world

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import xyz.qweru.geo.core.Global.mc
import xyz.qweru.geo.extend.minecraft.game.theLevel

operator fun BlockPos.plus(blockPos: BlockPos): BlockPos =
    this.offset(blockPos)
fun BlockPos.getAABBOf(face: Direction): AABB {
    val depth = 0.05
    val size = 1.0

    val x = this.x.toDouble()
    val y = this.y.toDouble()
    val z = this.z.toDouble()

    return when (face) {
        Direction.UP -> AABB(x, y + 1, z, x + size, y + 1 + depth, z + size)
        Direction.DOWN -> AABB(x, y - depth, z, x + size, y, z + size)
        Direction.NORTH -> AABB(x, y, z - depth, x + size, y + size, z)
        Direction.SOUTH -> AABB(x, y, z + 1, x + size, y + size, z + 1 + depth)
        Direction.WEST -> AABB(x - depth, y, z, x, y + size, z + size)
        Direction.EAST -> AABB(x + 1, y, z, x + 1 + depth, y + size, z + size)
    }
}

val BlockPos.state: BlockState
    get() = mc.theLevel.getBlockState(this)