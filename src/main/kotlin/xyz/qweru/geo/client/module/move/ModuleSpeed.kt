package xyz.qweru.geo.client.module.move

import net.minecraft.block.Blocks
import net.minecraft.block.entity.ChestBlockEntity
import net.minecraft.block.entity.EnderChestBlockEntity
import net.minecraft.block.entity.ShulkerBoxBlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.vehicle.BoatEntity
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket
import net.minecraft.util.PlayerInput
import xyz.qweru.geo.client.event.PostMoveSendEvent
import xyz.qweru.geo.client.event.PostMovementTickEvent
import xyz.qweru.geo.client.event.PreMoveSendEvent
import xyz.qweru.geo.client.event.PreMovementTickEvent
import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.client.helper.input.GameInput
import xyz.qweru.geo.client.helper.network.PacketHelper
import xyz.qweru.geo.client.helper.world.WorldHelper
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.manager.movement.MovementState
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.airTicks
import xyz.qweru.geo.extend.thePlayer
import xyz.qweru.geo.extend.theWorld
import xyz.qweru.geo.extend.withJump


class ModuleSpeed : Module("Speed", "bypass test", Category.MOVEMENT) {
    private val sg = settings.group("General")
    private val mode by sg.enum("Mode", "Speed mode", Mode.VULCAN)

    private val svulcan = settings.group("Vulcan").visible { mode == Mode.VULCAN }
    private val downVel by svulcan.float("Velocity", "Downwards velocity", -0.1f, -0.5f, 0.5f)
    private val hVel by svulcan.float("Velocity H", "Horizontal velocity", 0f, -0.5f, 0.5f)
    private val airTick by svulcan.int("Air Tick", "Which air tick to use", 6, 1, 10)

    private val sgrimA = settings.group("Grim A").visible { mode == Mode.GRIM_COLLIDE }
    private val extraCollide by sgrimA.float("Extra Collide", ".", 0.5f, 0f, 1f)
    private val ml by sgrimA.float("Mul", "fixing my skill issue", 0.3f, 0.1f, 2f)

    private val a by sg.int("a", ".", 1, 0, 5)
    private val eq by sg.enum("eq", ".", Compare.EQ)

    private var hardCollisionTicks = 0
    private val maxHardCollisionTicks = 3

    @Handler
    private fun onVelocity(e: PostMovementTickEvent) {
        when (mode) {
            Mode.GRIM_COLLIDE -> grimCollide(e)
            Mode.VULCAN -> vulcanSpeed(e)
            Mode.GRIM -> {}
        }
    }

    @Handler
    fun preMoveSend(e: PreMoveSendEvent) {
        if (!inGame || mode != Mode.GRIM) return
        GameInput.jumpKey = mc.thePlayer.isOnGround && GameInput.moving
    }
    @Handler
    fun postMoveSend(e: PostMoveSendEvent) {
        if (mode != Mode.GRIM) return
        if (mc.thePlayer.isOnGround || eq.act.invoke(mc.thePlayer.airTicks, a)) {
            PacketHelper.sendPacket(
                ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING)
            )
        }
    }

    fun grimCollide(e: PostMovementTickEvent) {
        val mul = getMaxOffset(0.0)
        e.velX += mul * ml * e.velX
        e.velY += mul * ml * e.velY
        e.velZ += mul * ml * e.velZ
    }

    fun vulcanSpeed(e: PostMovementTickEvent) {
        if (mc.thePlayer.airTicks == airTick) {
            e.velY += downVel
            val vec = mc.thePlayer.rotationVector
            e.velX += vec.x * hVel
            e.velZ += vec.z * hVel
        }
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
        return WorldHelper.playerIntersects(expand = extraCollide.toDouble()) { pos, state, box -> mc.theWorld.getBlockEntity(pos) is ShulkerBoxBlockEntity}
    }

    // TODO
    /**
     * Pre 1.9, near an anvil or any type of chest
     */
    fun isNearGlitchyBlock(): Boolean {
        return WorldHelper.playerIntersects(expand = extraCollide.toDouble()) { pos, state, box ->
            val entity = mc.theWorld.getBlockEntity(pos)
            entity is EnderChestBlockEntity || entity is ChestBlockEntity || state.isOf(Blocks.ANVIL)
        }
    }

    enum class Mode {
        VULCAN,
        GRIM,
        GRIM_COLLIDE
    }

    enum class Compare(val act: (Int, Int) -> Boolean) {
        EQ({a, b -> a == b}),
        LARGE({a, b -> a >= b}),
        SMALL({a, b -> a <= b})
    }
}