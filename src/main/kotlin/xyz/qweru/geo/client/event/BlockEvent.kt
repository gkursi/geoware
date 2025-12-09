package xyz.qweru.geo.client.event

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.phys.BlockHitResult
import xyz.qweru.geo.core.event.Cancellable

abstract class BlockEvent : Cancellable() {
    lateinit var pos: BlockPos
}

/**
 * Cancelling this won't cancel the block placement
 */
object PostPlaceBlockEvent : BlockEvent() {
    lateinit var hit: BlockHitResult
}

object AttackBlockEvent : BlockEvent() {
    lateinit var direction: Direction
}