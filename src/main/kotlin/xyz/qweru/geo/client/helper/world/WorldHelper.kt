package xyz.qweru.geo.client.helper.world

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.projectile.ProjectileUtil
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.VoxelShape
import xyz.qweru.geo.core.Core.mc
import xyz.qweru.geo.core.game.rotation.data.Rotation
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import xyz.qweru.geo.extend.minecraft.game.theLevel
import java.util.function.Predicate
import kotlin.math.floor


object WorldHelper {
    fun getCrosshairTarget(
        range: Double,
        rotation: FloatArray,
        wallRange: Double = range,
        ignoreBlocks: Boolean = false,
        filter: Predicate<Entity> = Predicate<Entity> { true },
        entity: Entity = mc.thePlayer,
        level: Level = mc.theLevel
    ): HitResult? {
        val cameraPos = entity.eyePosition

        val direction = Vec3.directionFromRotation(rotation[1], rotation[0])
        val offset = direction.scale(range)
        val wallOffset = direction.scale(wallRange)
        val targetPos = cameraPos.add(offset)
        val wallPos = cameraPos.add(wallOffset)

        val entityHitResult = ProjectileUtil.getEntityHitResult(
            entity,
            cameraPos,
            targetPos,
            entity.boundingBox.expandTowards(direction).inflate(1.0),
            filter.and { targetEntity -> !targetEntity.isSpectator },
            range * range
        )

        if (entityHitResult != null) {
            return entityHitResult
        }

        if (!ignoreBlocks) {
            val context = ClipContext(
                cameraPos,
                wallPos,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                entity
            )

            val hitResult: HitResult? = level.clip(context)
            if (hitResult != null && hitResult.type != HitResult.Type.MISS) {
                return hitResult
            }
        }

        return null
    }

    fun getCrosshairTarget(rotation: Rotation, range: Double) = getCrosshairTarget(range = range, rotation = floatArrayOf(rotation.yaw, rotation.pitch))

    fun playerIntersects(expand: Double = 0.0, validate: (BlockPos, BlockState, AABB) -> Boolean): Boolean {
        val bb = mc.thePlayer.boundingBox.inflate(expand)

        val minX = floor(bb.minX).toInt()
        val maxX = floor(bb.maxX).toInt()
        val minY = floor(bb.minY).toInt()
        val maxY = floor(bb.maxY).toInt()
        val minZ = floor(bb.minZ).toInt()
        val maxZ = floor(bb.maxZ).toInt()

        for (x in minX..maxX) {
            for (y in minY..maxY) {
                for (z in minZ..maxZ) {
                    val pos = BlockPos(x, y, z)
                    val state = mc.theLevel.getBlockState(pos)
                    val shape: VoxelShape = state.getCollisionShape(mc.theLevel, pos)

                    if (!shape.isEmpty) {
                        val shapeBox = shape.bounds().move(x.toDouble(), y.toDouble(), z.toDouble())
                        if (!validate.invoke(pos, state, shapeBox)) continue
                        if (bb.intersects(shapeBox)) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    fun blockCollision(world: Level, start: Vec3, end: Vec3): Boolean {
        val result: BlockHitResult = world.clip(
            ClipContext(
                start,
                end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                mc.thePlayer
            )
        )

        return result.type != HitResult.Type.MISS
    }

    fun blockCollision(world: Level, start: Vec3, vararg end: Vec3): Vec3? {
        for (v in end) {
            if (!blockCollision(world, start, v)) return v
        }

        return null
    }

    fun blockCollision(world: Level, start: Vec3, box: AABB) =
        blockCollision(world, start,
            box.center,
            Vec3(box.minX, box.minY, box.minZ),
            Vec3(box.maxX, box.minY, box.minZ),
            Vec3(box.minX, box.minY, box.maxZ),
            Vec3(box.maxX, box.minY, box.maxZ),
            Vec3(box.minX, box.maxY, box.minZ),
            Vec3(box.maxX, box.maxY, box.minZ),
            Vec3(box.minX, box.maxY, box.maxZ),
            Vec3(box.maxY, box.maxY, box.maxZ),
        )
}