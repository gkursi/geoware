package xyz.qweru.geo.client.event

import net.minecraft.world.phys.BlockHitResult
import xyz.qweru.geo.core.event.Cancellable

abstract class BlockEvent : Cancellable() {
    lateinit var hit: BlockHitResult
}

/**
 * Cancelling this won't cancel the block placement
 */
object PostPlaceBlockEvent : BlockEvent()