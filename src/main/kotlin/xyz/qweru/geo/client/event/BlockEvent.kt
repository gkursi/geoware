package xyz.qweru.geo.client.event

import net.minecraft.world.phys.BlockHitResult

abstract class BlockEvent {
    lateinit var hit: BlockHitResult
}

object PlaceBlockEvent : BlockEvent()