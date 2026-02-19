package xyz.qweru.geo.core.game.rotation.plan.impl

import xyz.qweru.geo.core.Core
import xyz.qweru.geo.core.game.rotation.data.Rotation
import xyz.qweru.geo.core.game.rotation.plan.RotationPlan
import xyz.qweru.geo.core.game.rotation.processor.ProcessorChain
import xyz.qweru.geo.core.game.rotation.processor.impl.FactorRotationProcessor

class SingleRotationPlan(
    val target: Rotation,
    val start: Rotation,
    override val priority: Int,
    val guh: Boolean = false
) : RotationPlan {
    private val pipeline = ProcessorChain(
        FactorRotationProcessor(0.9f..1f)
    )

    override fun nextStep(current: Rotation?): Rotation? {
        val current = current ?: start

        if (current.approxEquals(target)) {
            Core.logger.info("Stopped! (is player=$guh)")
            return null
        }

        val preDelta = current.deltaTo(target)

        val delta = pipeline.process(
            start,
            target,
            current
        )

        val postDelta = (current + delta).deltaTo(target)

//        if (guh) {
//            Core.logger.warn("pre=$preDelta\npost=$postDelta")
//        } else {
//            Core.logger.info("pre=$preDelta\npost=$postDelta")
//        }

//        Core.logger.info("pipe produced delta=$delta")
//        Core.logger.info("will result in ${current + delta}")

        return current + delta
    }
}