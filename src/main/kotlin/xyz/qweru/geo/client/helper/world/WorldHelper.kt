package xyz.qweru.geo.client.helper.world

import net.fabricmc.loader.impl.lib.sat4j.core.Vec
import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.ProjectileUtil
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.RaycastContext
import net.minecraft.world.World
import xyz.qweru.geo.core.Global.mc
import xyz.qweru.geo.extend.thePlayer
import xyz.qweru.geo.extend.theWorld
import java.util.function.Predicate
import kotlin.math.floor


object WorldHelper {
    // https://github.com/Eglijohn/brew-addon/blob/1.21.8/src/main/java/blub/brewaddon/utils/misc/HitResults.java#L18
    fun getCrosshairTarget(
        entity: Entity = mc.thePlayer,
        range: Double,
        ignoreBlocks: Boolean = false,
        filter: Predicate<Entity> = Predicate<Entity> { true },
        rotation: FloatArray
    ): HitResult? {
        val cameraPos = entity.eyePos

        val direction = Vec3d.fromPolar(rotation[0], rotation[1]).multiply(range)
        val targetPos = cameraPos.add(direction)

        val entityHitResult = ProjectileUtil.raycast(
            entity,
            cameraPos,
            targetPos,
            entity.boundingBox.stretch(direction).expand(1.0),
            filter.and { targetEntity -> !targetEntity.isSpectator },
            range * range
        )

        if (entityHitResult != null) {
            return entityHitResult
        }

        if (!ignoreBlocks) {
            val context = RaycastContext(
                cameraPos,
                targetPos,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                entity
            )

            val hitResult: HitResult? = mc.theWorld.raycast(context)
            if (hitResult != null && hitResult.type != HitResult.Type.MISS) {
                return hitResult
            }
        }

        return null
    }

    fun playerIntersects(expand: Double = 0.0, validate: (BlockPos, BlockState, Box) -> Boolean): Boolean {
        val bb = mc.thePlayer.boundingBox.expand(expand)

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
                    val state = mc.theWorld.getBlockState(pos)
                    val shape: VoxelShape = state.getCollisionShape(mc.theWorld, pos)

                    if (!shape.isEmpty) {
                        val shapeBox = shape.boundingBox.offset(x.toDouble(), y.toDouble(), z.toDouble())
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

    fun blockCollision(world: World, start: Vec3d?, end: Vec3d?): Boolean {
        val result: BlockHitResult = world.raycast(
            RaycastContext(
                start,
                end,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                mc.player
            )
        )

        return result.type != HitResult.Type.MISS
    }

    fun blockCollision(world: World, start: Vec3d?, vararg end: Vec3d?): Vec3d? {
        for (v in end) {
            if (!blockCollision(world, start, v)) return v
        }

        return null
    }

    fun blockCollision(world: World, start: Vec3d?, box: Box) =
        blockCollision(world, start,
            box.center,
            Vec3d(box.minX, box.minY, box.minZ),
            Vec3d(box.maxX, box.minY, box.minZ),
            Vec3d(box.minX, box.minY, box.maxZ),
            Vec3d(box.maxX, box.minY, box.maxZ),
            Vec3d(box.minX, box.maxY, box.minZ),
            Vec3d(box.maxX, box.maxY, box.minZ),
            Vec3d(box.minX, box.maxY, box.maxZ),
            Vec3d(box.maxY, box.maxY, box.maxZ),
        )
}