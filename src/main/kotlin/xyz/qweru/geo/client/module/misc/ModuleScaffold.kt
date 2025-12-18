package xyz.qweru.geo.client.module.misc

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.minecraft.game.theLevel
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import xyz.qweru.geo.extend.minecraft.world.isBottom
import xyz.qweru.geo.extend.minecraft.world.sqrtSpeed
import xyz.qweru.geo.extend.minecraft.world.state

class ModuleScaffold : Module("Scaffold", "Automatically places blocks below you", Category.MISC) {
    private val sg = settings.group("General")

    private var y = 0
    private var target: BlockHitResult? = null

    override fun enable() {
        if (!inGame) {
            enabled = false
            return
        }
        target = null
        y = mc.thePlayer.blockY - 1
    }

    @Handler
    private fun tick(e: PreTickEvent) {
        if (!inGame) return
    }

    private fun scan() {
        if (target != null) return

        val ix = mc.thePlayer.blockX
        val iz = mc.thePlayer.blockX
        val base = BlockPos(ix, y, iz)
        val eye = mc.thePlayer.eyePosition

        if (!base.state.canBeReplaced()) return

        var closest: BlockHitResult?
        var distance = Int.MAX_VALUE

        for (x in -3..3) {
            for (z in -3..3) {
                val pos = BlockPos(ix + x, y, iz + z)
                val hit = getHitResult(pos) ?: continue
                if (eye.distanceToSqr(pos.center) > 9) continue

                val dist = pos.center.distanceToSqr(base.center)
                if (dist < distance) {

                }
            }
        }
    }

    private fun getHitResult(pos: BlockPos): BlockHitResult? {
        val state = pos.state
        if (!state.canBeReplaced()) return null
        val side = findSolidSide(pos) ?: return null
        return BlockHitResult(pos.center, side, pos, false)
    }

    private fun findSolidSide(pos: BlockPos): Direction? {
        for (direction in Direction.entries) {
            val offset = pos.relative(direction)
            if (direction.isBottom || !offset.state.isFaceSturdy(mc.theLevel, offset, direction.opposite)) continue
            return direction
        }
        return null
    }

    enum class Mode {
        GRIM
    }
}