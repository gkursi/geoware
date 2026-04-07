package xyz.qweru.geo.core.game.rotation.plan.impl

import xyz.qweru.geo.client.helper.math.function.CombinedFunction
import xyz.qweru.geo.client.helper.math.function.SigmoidFunction
import xyz.qweru.geo.core.Core
import xyz.qweru.geo.core.game.rotation.data.Rotation
import xyz.qweru.geo.core.game.rotation.plan.RotationPlan
import xyz.qweru.geo.core.game.rotation.processor.ProcessorChain
import xyz.qweru.geo.core.game.rotation.processor.impl.FactorRotationProcessor
import xyz.qweru.geo.core.game.rotation.processor.impl.FunctionRotationProcessor

class SingleRotationPlan(
    val target: Rotation,
    val start: Rotation,
    override val priority: Int,
    val guh: Boolean = false
) : RotationPlan {
    private val pipeline = ProcessorChain(
        FunctionRotationProcessor(
            CombinedFunction(
                arrayOf(
                    SigmoidFunction(),
                ),
                floatArrayOf(
                    1f,
                )
            )
        ),
        FactorRotationProcessor(0.9f..1f),
//        VelocityRotationProcessor,
//        StopRotationProcessor(50f, 10f)
    )

    override fun nextStep(current: Rotation?): Rotation? {
        val current = current ?: start

        if (current.approxEquals(target)) {
            Core.logger.info("Stopped! (is player=$guh)")
            return null
        }

        val delta = pipeline.process(
            start,
            target,
            current
        )

        return current + delta
    }
}