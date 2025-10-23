package xyz.qweru.geo.client.module.move

import net.minecraft.block.Blocks
import net.minecraft.block.entity.ChestBlockEntity
import net.minecraft.block.entity.EnderChestBlockEntity
import net.minecraft.block.entity.ShulkerBoxBlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.vehicle.BoatEntity
import xyz.qweru.geo.client.event.VelocityTickEvent
import xyz.qweru.geo.client.helper.world.WorldHelper
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.manager.movement.MovementState
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.airTicks
import xyz.qweru.geo.extend.thePlayer
import xyz.qweru.geo.extend.theWorld

class ModuleSpeed : Module("Speed", "bypass test", Category.MOVEMENT) {
    private val sg = settings.group("General")
    private val mode by sg.enum("Mode", "Speed mode", Mode.VULCAN)

    private val svulcan = settings.group("Vulcan").visible { mode == Mode.VULCAN }
    private val downVel by svulcan.float("Velocity", "Downwards velocity", -0.125f, -0.5f, 0.5f)
    private val airTick by svulcan.int("Air Tick", "Which air tick to use", 6, 1, 10)

    private val sgrim = settings.group("Grim").visible { mode == Mode.GRIM }
    private val extraCollide by sgrim.float("Extra Collide", ".", 0.5f, 0f, 1f)
    private val ml by sgrim.float("Mul", "fixing my skill issue", 0.3f, 0.1f, 2f)

    private var hardCollisionTicks = 0
    private val maxHardCollisionTicks = 3

    @Handler
    private fun onVelocity(e: VelocityTickEvent) {
        when (mode) {
            Mode.GRIM -> grimSpeed(e)
            Mode.VULCAN -> vulcanSpeed(e)
        }
    }

    fun grimSpeed(e: VelocityTickEvent) {
        val mul = getMaxOffset(0.0)
        e.x += mul * ml * e.x
        e.y += mul * ml * e.y
        e.z += mul * ml * e.z
    }

    fun vulcanSpeed(e: VelocityTickEvent) {
        if (mc.thePlayer.airTicks == airTick) e.y += downVel
    }

    fun getMaxOffset(base: Double): Double {
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

    enum class Mode {
        VULCAN,
        GRIM
    }
}