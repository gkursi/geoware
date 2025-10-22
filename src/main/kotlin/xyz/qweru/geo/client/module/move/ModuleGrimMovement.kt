package xyz.qweru.geo.client.module.move

import net.minecraft.block.Blocks
import net.minecraft.block.entity.ChestBlockEntity
import net.minecraft.block.entity.EnderChestBlockEntity
import net.minecraft.block.entity.ShulkerBoxBlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.vehicle.BoatEntity
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import xyz.qweru.geo.client.event.PacketSendEvent
import xyz.qweru.geo.client.event.PostTickEvent
import xyz.qweru.geo.client.event.VelocityTickEvent
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.manager.movement.MovementState
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.thePlayer
import xyz.qweru.geo.extend.theWorld
import xyz.qweru.geo.client.helper.world.WorldHelper

class ModuleGrimMovement : Module("GrimMovement", "grim bypass test", Category.MOVEMENT) {

    val sg = settings.group("General")
    private var extraCollide by sg.float("Extra Collide", ".", 0.5f, 0f, 1f)
    private var ml by sg.float("Mul", "fixing my skill issue", 0.3f, 0.1f, 2f)

    private var hardCollisionTicks = 0
    private val maxHardCollisionTicks = 3

    private var packet = 0
    private var tick = 0

    @Handler
    private fun postTick(e: PostTickEvent) {
        if (!inGame) return
        if (packet != ++tick) {
            if (packet < tick) logger.warn("Skipped packet on tick $tick")
            else logger.warn("Too many packets on tick $tick")
            packet = tick
        }
    }

    @Handler
    private fun onPacket(e: PacketSendEvent) {
        if (e.packet is PlayerMoveC2SPacket) packet++
    }

    override fun enable() {
        packet = 0
        tick = 0
    }

    @Handler
    private fun onVelocity(e: VelocityTickEvent) {
        val mul = getMaxOffset(0.0)
        e.x += mul * ml * e.x
        e.y += mul * ml * e.y
        e.z += mul * ml * e.z
    }

    fun getMaxOffset(base: Double): Double {
        // This applies to input velocity, explosions and knockback

        var offset = base

        if (grimCollision()) {
            offset += 1
        }

        if (isNearGlitchyBlock()) {
            offset += 0.25
        }

        if (MovementState.slowedByBlock) {
            offset += 0.01
        }

        if (MovementState.bounce) {
            offset += 0.03
        }

        return offset
    }

    fun grimCollision(): Boolean {
        val box = mc.thePlayer.boundingBox.expand(extraCollide.toDouble())
        val riding = mc.thePlayer.vehicle
        for (entity in mc.theWorld.entities) {
            if (entity == mc.player || entity == riding || entity !is BoatEntity) continue
            if (entity.boundingBox.intersects(box)) {
                hardCollisionTicks = 0
                return true
            }
        }

        if (shulkerCollision(mc.thePlayer)) {
            hardCollisionTicks = 0
            return true
        }

        hardCollisionTicks++
        return hardCollisionTicks < maxHardCollisionTicks
    }

    fun shulkerCollision(player: PlayerEntity): Boolean {
        return WorldHelper.playerIntersects { pos, state, box -> mc.theWorld.getBlockEntity(pos) is ShulkerBoxBlockEntity}
    }

    // TODO
    /**
     * Pre 1.9, near an anvil or any type of chest
     */
    fun isNearGlitchyBlock(): Boolean {
        return WorldHelper.playerIntersects { pos, state, box ->
            val entity = mc.theWorld.getBlockEntity(pos)
            entity is EnderChestBlockEntity || entity is ChestBlockEntity || state.isOf(Blocks.ANVIL)
        }
    }

}