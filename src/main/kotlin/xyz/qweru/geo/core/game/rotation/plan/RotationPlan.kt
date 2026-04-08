package xyz.qweru.geo.core.game.rotation.plan

import xyz.qweru.geo.core.game.rotation.data.Rotation

interface RotationPlan {
    val priority: Int

    fun nextStep(current: Rotation?): Rotation?
}