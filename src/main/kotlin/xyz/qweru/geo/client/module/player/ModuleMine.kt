package xyz.qweru.geo.client.module.player

import com.google.common.collect.EvictingQueue
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.BlockHitResult
import xyz.qweru.geo.abstraction.game.Hand
import xyz.qweru.geo.client.event.AttackBlockEvent
import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.client.helper.network.PacketHelper
import xyz.qweru.geo.client.helper.timing.TimerDelay
import xyz.qweru.geo.client.helper.world.BlockHelper
import xyz.qweru.geo.client.helper.world.WorldHelper
import xyz.qweru.geo.core.Core.mc
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.minecraft.game.theLevel
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import xyz.qweru.geo.extend.minecraft.world.within
import java.util.*

// todo fix raycasting, breaking air, doublemine
class ModuleMine : Module("Mine", "Automatically mine blocks", Category.PLAYER) {

    private val sp = settings.group("Pick")
    private val pickMode by sp.enum("Pick Mode", "Mode for picking blocks to mine", PickMode.SELECT_ONCE)
    @Suppress("UNUSED")
    private val pickLimit by sp.int("Pick Limit", "Max amount of blocks you can pick at once", 3, 1, 15)
        .onChange { pickMode.onPickLimit(it.value) }

    private val sm = settings.group("Mine")
    private val speed by sm.float("Speed", "Mine speed", 1f, 0.7f, 1f)
    private val doubleMine by sm.boolean("Double Mine", "Allow double mine", false)
    private val assumeInstant by sm.boolean("Assume Instant", "Assume we can instantly mine any block", false)
    private val delay by sm.longRange("Delay", "Delay between swapping positions", 50L..100L, 0L..500L)
    private val raycast by sm.boolean("Raycast", "Raycast blocks before mining", true)
    private val reach by sm.float("Reach", "Mining reach", 3f, 1f, 6f)
    private val wallReach by sm.float("Wall Reach", "Mining reach trough walls", 0f, 1f, 6f)
    private val swing by sm.boolean("Swing", "Swing", true)
    private val silentSwing by sm.boolean("Silent Swing", "Swing silently", true)
        .visible { swing }

    private val primary = Miner { true }
    private val secondary = Miner { doubleMine }

    override fun enable() {
        primary.reset()
        secondary.reset()
    }

    @Handler
    private fun attackBlock(e: AttackBlockEvent) {
        pickMode.onAttackBlock(e.pos, e.direction)
        e.cancelled = true
    }

    @Handler
    private fun preTick(e: PreTickEvent) {
        if (!inGame) return
        primary.tick()
        secondary.tick()
    }

    private inner class Miner(private val condition: () -> Boolean) {
        private var mining = false
        private val timer = TimerDelay()
        private val canMine: Boolean
            get() = condition.invoke() && checkBlock()
        var block: StoredBlock? = null
            private set
        var progress = 0.0

        fun tick() {
            if (!canMine) {
                cancel()
                block = null
                return
            }

            mc.thePlayer.displayClientMessage(Component.literal("did not cancel"), false)

            finishBreaking()
            findNewBlock()

            if (block == null) return

            mine()
        }

        fun reset() {
            mining = false
            progress = 0.0
            block = null
            timer.reset()
        }

        private fun checkBlock(): Boolean {
            return if (block == null) true
                   else if (isAirOrLiquid()) false
                   else if (!block!!.pos.within(reach.toDouble())) false
                   else if (raycast && !raycastBlock()) false
                   else true
        }

        private fun isAirOrLiquid(): Boolean {
            val state = block?.state ?: return false
            return state.isAir || state.liquid()
        }

        private fun raycastBlock(): Boolean = block!!.pos.within(wallReach.toDouble()) &&
            WorldHelper.blockCollision(mc.theLevel, mc.thePlayer.eyePosition, AABB(block!!.pos)) != null

        private fun mine() {
            mining = true
            if (progress == 0.0) {
                sendAction(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK)
                progress += 0.000001
                return
            }

            if (assumeInstant) {
                progress += 1
                finishBreaking()
            } else {
                progress += BlockHelper.getBreakDelta(block!!.state)
            }
        }

        private fun finishBreaking() {
            if (progress < speed) return
            sendAction(ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK)
            mc.thePlayer.displayClientMessage(Component.literal("Finished breaking: $block"), false)
            reset()
        }

        private fun findNewBlock() {
            if (!timer.hasPassed() || block != null) return
            block = pickMode.nextBlock()
            if (block == null || !checkBlock()) return
            mc.thePlayer.displayClientMessage(Component.literal("Found block: $block"), false)
        }

        private fun cancel() {
            if (!mining) return
            sendAction(ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK)
            mc.thePlayer.displayClientMessage(Component.literal("Cancelled"), false)
            reset()
        }

        private fun sendAction(action: ServerboundPlayerActionPacket.Action) {
            mc.thePlayer.displayClientMessage(Component.literal("sent $action"), false)
            if (swing)
                PacketHelper.swing(Hand.MAIN_HAND, silentSwing)
            if (action == ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK)
                PacketHelper.sendPacket(ServerboundPlayerActionPacket(action, block?.pos ?: return, block?.direction ?: return))
            else
                PacketHelper.sendSequencedPacket { ServerboundPlayerActionPacket(action, block!!.pos, block!!.direction, it) }
        }
    }

    @Suppress("UNUSED")
    enum class PickMode {
        CROSSHAIR {
            override fun nextBlock(): StoredBlock? {
                val hit = mc.hitResult
                return if (hit !is BlockHitResult) null
                       else StoredBlock(hit.blockPos, hit.direction)
            }
        }, SELECT_ONCE {
            var limit = 3
            val queue = ArrayDeque<StoredBlock>(3)

            override fun nextBlock(): StoredBlock? = queue.poll()

            override fun onPickLimit(limit: Int) {
                this.limit = limit
            }

            override fun onAttackBlock(pos: BlockPos, direction: Direction) {
                if (queue.size >= limit) return
                queue.add(StoredBlock(pos, direction))
            }
        }, SELECT_CYCLE {
            var queue: EvictingQueue<StoredBlock> = EvictingQueue.create(3)

            override fun nextBlock(): StoredBlock? =
                queue.poll()?.apply { queue.add(this) }

            override fun onPickLimit(limit: Int) {
                queue = EvictingQueue.create(3)
            }

            override fun onAttackBlock(pos: BlockPos, direction: Direction) {
                if (queue.contains(StoredBlock(pos, direction))) return
                queue.add(StoredBlock(pos, direction))
            }
        }, AUTO {
            override fun nextBlock(): StoredBlock? = null // crazy algorithm
        };

        abstract fun nextBlock(): StoredBlock?

        open fun onAttackBlock(pos: BlockPos, direction: Direction) {}
        open fun onPickLimit(limit: Int) {}
    }

    data class StoredBlock(val pos: BlockPos, val direction: Direction) {
        val state: BlockState
            get() = mc.theLevel.getBlockState(pos)

        override fun toString(): String = "StoredBlock[pos=$pos, direction=$direction]"

        override fun equals(other: Any?): Boolean =
            if (other is StoredBlock) {
                pos == other.pos
            } else super.equals(other)

        override fun hashCode(): Int = pos.hashCode()
    }
}