package xyz.qweru.geo.core.game.rotation

import xyz.qweru.geo.core.Core
import xyz.qweru.geo.core.Core.mc
import xyz.qweru.geo.core.game.rotation.data.Rotation
import xyz.qweru.geo.core.game.rotation.plan.RotationPlan
import xyz.qweru.geo.core.game.rotation.plan.impl.SingleRotationPlan
import xyz.qweru.geo.extend.kotlin.array.copyRotation
import xyz.qweru.geo.extend.minecraft.entity.rotation
import xyz.qweru.geo.extend.minecraft.game.thePlayer

object RotationManager {
    var currentPlan: RotationPlan? = null
        private set

    var lastRotation: Rotation? = null
        get() = field ?: mc.thePlayer.rotation
        private set

    var currentRotation: Rotation? = null

    private val playerRotationPlan: RotationPlan
        get() = SingleRotationPlan(
            mc.thePlayer.rotation,
            lastRotation!!,
            -10,
            true
        )

    internal fun update() {
        val plan = currentPlan
            ?: playerRotationPlan.also {
                Core.logger.warn("rotating back")
            }

        val previous = currentRotation

        currentRotation = plan.nextStep(currentRotation)
            ?.fix()
            ?.also { nextRotation ->
                RotationHandler.serverRotation
                    .copyRotation(nextRotation)
            }
            ?: null.also {
                currentPlan = null
            }

        lastRotation = previous ?: return
    }

    fun propose(rotation: Rotation, priority: Int) {
        val current = currentPlan
            ?.priority
            ?: Int.MIN_VALUE

        if (priority < current) {
            return
        }

        currentPlan = SingleRotationPlan(
            rotation,
            lastRotation!!,
            priority
        )
    }
}