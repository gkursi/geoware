package xyz.qweru.geo.client.module.misc

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket
import net.minecraft.util.Mth
import net.minecraft.world.item.BlockItem
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import org.lwjgl.glfw.GLFW
import xyz.qweru.geo.abstraction.game.GameOptions
import xyz.qweru.geo.client.event.PacketSendEvent
import xyz.qweru.geo.client.event.PreCrosshair
import xyz.qweru.geo.client.event.PreMovementTickEvent
import xyz.qweru.geo.client.helper.math.random.LayeredRandom
import xyz.qweru.geo.client.helper.player.RotationHelper
import xyz.qweru.geo.client.helper.player.inventory.InvHelper
import xyz.qweru.geo.client.helper.timing.TimerDelay
import xyz.qweru.geo.client.helper.world.WorldHelper
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.manager.rotation.Rotation
import xyz.qweru.geo.core.manager.rotation.RotationConfig
import xyz.qweru.geo.core.manager.rotation.RotationHandler
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.minecraft.entity.airTicks
import xyz.qweru.geo.extend.minecraft.entity.distanceFromEyesSq
import xyz.qweru.geo.extend.minecraft.entity.isOnGround
import xyz.qweru.geo.extend.minecraft.game.reverse
import xyz.qweru.geo.extend.minecraft.game.theLevel
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import xyz.qweru.geo.extend.minecraft.game.withJump
import xyz.qweru.geo.extend.minecraft.world.getAABBOf
import xyz.qweru.geo.extend.minecraft.world.isVertical
import xyz.qweru.geo.extend.minecraft.world.plus
import xyz.qweru.geo.extend.minecraft.world.state
import xyz.qweru.geo.mixin.network.packet.ServerboundPlayerInputPacketAccessor
import xyz.qweru.multirender.api.API
import xyz.qweru.multirender.api.input.Input
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class ModuleScaffold : Module("Scaffold", "Automatically places blocks below you", Category.MISC) {
    private val sg = settings.group("General")
    private val saa = settings.group("Auto Aim")
    private val sap = settings.group("Auto Place")

    private val aim by sg.enum("Aim", "Aim mode", Action.AUTO)
    private val place by sg.enum("Place", "Place mode", Action.AUTO)
    private val airPlace by sg.boolean("Air Place", "Assumes airplacing", false)
    private val raycast by sg.boolean("Raycast", "Raycasts possible positions", true)
    private val limit by sg.boolean("Limit", "Prevents flagging limit on certain anticheats", false)
    private val limitTicks by sg.int("Limit Ticks", "Delay between limiting", 10, 1, 40)

    private val rotations by saa.enum("Rotation", "Rotation mode", Rotate.NORMAL)
    private val target by saa.enum("Target", "Aim target", FaceTarget.CENTER_NEAR)
        .visible { rotations == Rotate.NORMAL }
    private val offset by saa.floatRange("Offset", "Offset for the position", -0.04f..0.04f, -0.2f..0.2f)
    private val inhibit by saa.boolean("Inhibit", "Reduces the amount of rotations by constantly rotating", false)
    private val inhibitTimeout by saa.longRange("Timeout", "Stops inhibiting after this amount of time", 200L..250L, 0L..1000L)
        .visible { inhibit }

    private val reach by sap.float("Reach", "Reach for block placement", 3f, 1f, 6f)
    private val delay by sap.longRange("Delay", "Place delay", 90L..105L, 0L..250L)
    private val airTicks by sap.int("Air Ticks", "Required ticks to place when in air", 5, 0, 10)

    private val rotationConfig = RotationConfig(mouseFix = true)
    private val inhibitTimer = TimerDelay()
    private val placeTimer = TimerDelay()
    private var currentTarget: Vec3? = null
    private var shouldPlace = true
    private var currentRotation: Rotation? = null
    private var limitCounter = 0

    @Handler
    private fun preCrosshair(e: PreCrosshair) {
        if (!inGame) return
        findTarget()
        rotate()
    }

    @Handler
    private fun preCross(e: PreCrosshair) {
        if (!inGame || place == Action.MANUAL || !isValidRotation() || !placeTimer.hasPassed() || awaitGround()) return
        InvHelper.swap({ it.item is BlockItem }, priority = 10)
        if (!InvHelper.isInMainhand { it.item is BlockItem }) return
        API.mouseHandler.input(GLFW.GLFW_MOUSE_BUTTON_2, Input.CLICK)
        placeTimer.reset(delay)
    }

    @Handler
    private fun preMove(e: PreMovementTickEvent) {
        if (!limit) return
        limitCounter++
        limitCounter %= max(limitTicks, 2)
        if (limitCounter == 0) {
            GameOptions.sneakKey = true
        } else {
            GameOptions.syncBind(GameOptions::sneakKey)
        }
    }

    @Handler
    private fun onPacketSend(e: PacketSendEvent) {
        val packet = e.packet
        if (packet !is ServerboundPlayerInputPacket) return
        (packet as ServerboundPlayerInputPacketAccessor).geo_setInput(packet.input.reverse())
    }

    private fun awaitGround(): Boolean =
        !mc.thePlayer.isOnGround && mc.thePlayer.airTicks < airTicks

    private fun findTarget() {
        if (isValidRotation()) return
        val random = LayeredRandom.DEFAULT
        val next = target.find(this)
            ?.add(random.float(offset).toDouble(), random.float(offset).toDouble(), random.float(offset).toDouble())
        if (next != null || inhibitTimer.hasPassed()) {
            currentTarget = next
            currentRotation = currentTarget?.let { RotationHelper.get(it, rotationConfig) }
            inhibitTimer.reset(inhibitTimeout)
        }
        shouldPlace = next != null || !inhibit || inhibitTimer.hasPassed()
    }

    private fun rotate() {
        if (aim != Action.AUTO) return
        RotationHandler.propose(
            currentRotation ?: return,
            Rotation.IMPORTANT_BLOCK
        )
    }

    private fun isValidRotation(): Boolean =
        currentRotation?.let {
            if(!RotationHandler.isLookingAt(it)) return@let false
            val hit = mc.hitResult
            return@let hit is BlockHitResult && !hit.direction.isVertical
        } ?: false

    private fun validate(pos: BlockPos): Boolean {
        if (rotations != Rotate.SNAP) return true
        val dx = abs(pos.x - mc.thePlayer.blockX)
        val dy = abs(pos.y - mc.thePlayer.blockY)
        return dx == 0 || dy == 0 || dx == dy || dx + 1 == dy || dx == dy + 1
    }

    private fun optimalCenter(pos: BlockPos): Vec3? {
        if (airPlace) return pos.center
        for (direction in Direction.entries) {
            val offset = pos.relative(direction)
            val opposite = direction.opposite

            if (direction.isVertical) continue
            if (offset.state.canBeReplaced()) continue

            return if (raycast) WorldHelper.blockCollision(
                mc.theLevel, mc.thePlayer.eyePosition, offset.getAABBOf(opposite)
            ) ?: continue // only return if we successfully raycast
            else offset.center.add(
                opposite.stepX * .5, opposite.stepY * .5, opposite.stepZ * .5
            )
        }
        return null
    }

    private enum class Action {
        AUTO, MANUAL
    }

    private enum class FaceTarget(val block: ModuleScaffold.() -> Vec3?) {
        CENTER_NEAR({
            val range = reach.roundToInt()
            val rangeSq = Mth.square(reach)
            var nearest: Vec3? = null
            var nearestDist = Double.MAX_VALUE

            for (x in -range..range) {
                for (y in -3..-1) {
                    for (z in -range..range) {
                        val pos = BlockPos(x, y, z) + mc.thePlayer.blockPosition()
                        val center = optimalCenter(pos) ?: continue
                        val state by lazy { mc.theLevel.getBlockState(pos) }
                        val distance = mc.thePlayer.distanceFromEyesSq(center)

                        if (distance > rangeSq || distance > nearestDist) continue
                        if (!validate(pos) || !state.canBeReplaced()) continue

                        nearest = center
                        nearestDist = distance
                    }
                }
            }

            nearest
        }),
        CENTER_BELOW({
            val pos = mc.thePlayer.blockPosition().below()
            if (!validate(pos) || !pos.state.canBeReplaced()) null
            else optimalCenter(pos)
        });
//        VISIBLE_FACE({
//
//        });

        fun find(m: ModuleScaffold) = block.invoke(m)
    }

    private enum class Rotate {
        NORMAL, SNAP
    }
}