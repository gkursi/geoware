package xyz.qweru.geo.client.module.combat

import it.unimi.dsi.fastutil.objects.ObjectArraySet
import net.minecraft.core.BlockPos
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.RespawnAnchorBlock
import net.minecraft.world.phys.BlockHitResult
import org.lwjgl.glfw.GLFW
import xyz.qweru.geo.client.event.PacketReceiveEvent
import xyz.qweru.geo.client.event.PlaceBlockEvent
import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.minecraft.game.theLevel
import xyz.qweru.geo.client.helper.player.inventory.InvHelper
import xyz.qweru.geo.client.helper.timing.TimerDelay
import xyz.qweru.geo.extend.minecraft.item.isOf
import xyz.qweru.geo.extend.minecraft.world.isOf
import xyz.qweru.multirender.api.API
import xyz.qweru.multirender.api.input.Input

class ModuleAnchorMacro : Module("AnchorMacro", "Automatically place and break anchors", Category.COMBAT) {
    val sa = settings.group("Actions")
    val fillAnchor by sa.boolean("Fill", "Place anchor", true)
    val fillDelay by sa.longRange("Fill Delay", "Delay for filling", 0L..5, 0L..500L)
    val breakAnchor by sa.boolean("Break", "Break anchor", true)
    val breakDelay by sa.longRange("Break Delay", "Delay for breaking", 0L..5, 0L..500L)
    val onlyOwn by sa.boolean("Only Own", "Only break anchors placed by you", true)
    val replace by sa.boolean("Replace", "Replace broken anchors", true)

    private val fillTimer = TimerDelay()
    private val breakTimer = TimerDelay()
    private var initSlot = -1
    private val brokenAnchors = ObjectArraySet<BlockPos>()
    private val placedAnchors = ObjectArraySet<BlockPos>()

    @Handler
    private fun preTick(e: PreTickEvent) {
        if (!inGame) return

        val hit = mc.hitResult
        if (hit !is BlockHitResult) return
        val state = mc.theLevel.getBlockState(hit.blockPos)
        if (!state.isOf(Blocks.RESPAWN_ANCHOR)) return
        if (onlyOwn && !placedAnchors.contains(hit.blockPos)) return

        val charges = state.getValue(RespawnAnchorBlock.CHARGE)
        if (charges == 0) place(Items.GLOWSTONE, fillTimer, fillDelay)
        else breakPos(hit)
    }

    @Handler
    private fun onPlaceBlock(e: PlaceBlockEvent) {
        if (!InvHelper.isHolding(Items.RESPAWN_ANCHOR)) return
        val hit = e.hit
        var pos = hit.blockPos
        val state = mc.theLevel.getBlockState(pos)
        if (!state.canBeReplaced() && !state.isOf(Blocks.RESPAWN_ANCHOR)) pos = pos.relative(hit.direction)
        placedAnchors.add(pos)
    }

    @Handler
    private fun onPacket(e: PacketReceiveEvent) {
        val packet = e.packet
        if (packet !is ClientboundBlockUpdatePacket) return
        if (packet.blockState.isOf(Blocks.RESPAWN_ANCHOR)) return
        placedAnchors.remove(packet.pos)
        brokenAnchors.remove(packet.pos)
    }

    private fun place(item: Item, timer: TimerDelay, delay: LongRange) {
        if (!fillAnchor) return
        if (!InvHelper.isInMainhand(item)) {
            initSlot = InvHelper.selectedSlot
            InvHelper.swap(item, 50)
            timer.reset(delay)
            return
        }
        if (!timer.hasPassed()) return
        API.mouseHandler.input(GLFW.GLFW_MOUSE_BUTTON_2, Input.CLICK)
    }

    private fun breakPos(hit: BlockHitResult) {
        if (!breakAnchor) return
        if (brokenAnchors.contains(hit.blockPos)) return
        if (InvHelper.isInMainhand(Items.GLOWSTONE)) {
            if (initSlot == -1) InvHelper.swap({ !it.isOf(Items.GLOWSTONE) }, 50)
            else InvHelper.swap(initSlot, 50)
            if (InvHelper.selectedSlot == initSlot) initSlot = -1
            breakTimer.reset(breakDelay)
            return
        }
        if (!breakTimer.hasPassed()) return
        API.mouseHandler.input(GLFW.GLFW_MOUSE_BUTTON_2, Input.CLICK)
        brokenAnchors.add(hit.blockPos)
    }

}