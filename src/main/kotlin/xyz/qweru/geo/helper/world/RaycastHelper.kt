package xyz.qweru.geo.helper.world

import net.minecraft.client.render.Camera
import net.minecraft.entity.Entity
import net.minecraft.entity.projectile.ProjectileUtil
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RaycastContext
import xyz.qweru.geo.core.Glob.mc
import xyz.qweru.geo.extend.thePlayer
import xyz.qweru.geo.extend.theWorld
import java.util.function.Predicate

object RaycastHelper {
    // https://github.com/Eglijohn/brew-addon/blob/1.21.8/src/main/java/blub/brewaddon/utils/misc/HitResults.java#L18
    fun getCrosshairTarget(
        entity: Entity = mc.thePlayer,
        range: Double,
        ignoreBlocks: Boolean = false,
        filter: Predicate<Entity> = Predicate<Entity> { true }
    ): HitResult? {
        val camera: Camera = mc.gameRenderer.camera
        val cameraPos = camera.pos

        val direction = Vec3d.fromPolar(camera.pitch, camera.yaw).multiply(range)
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
}