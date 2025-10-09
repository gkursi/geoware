package xyz.qweru.geo.client.event

import net.minecraft.util.hit.BlockHitResult

abstract class BlockEvent {
    lateinit var hit: BlockHitResult
}

object PlaceBlockEvent : BlockEvent()