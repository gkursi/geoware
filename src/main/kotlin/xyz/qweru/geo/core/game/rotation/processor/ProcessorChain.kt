package xyz.qweru.geo.core.game.rotation.processor

import xyz.qweru.geo.client.helper.math.random.LayeredRandom
import xyz.qweru.geo.core.game.rotation.data.Rotation

class ProcessorChain(vararg val processors: RotationProcessor) {
    private val layeredRandom = LayeredRandom.DEFAULT

    /**
     * Applies all given processors
     * @return a rotation delta
     */
    fun process(
        start: Rotation,
        end: Rotation,
        current: Rotation
    ): Rotation {
        var delta = current.deltaTo(end)

        for (processor in processors) {
            delta = processor.process(
                start,
                end,
                current,
                delta,
                layeredRandom
            )
        }

        return delta
    }

}