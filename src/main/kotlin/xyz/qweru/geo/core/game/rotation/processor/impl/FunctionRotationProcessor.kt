package xyz.qweru.geo.core.game.rotation.processor.impl

import xyz.qweru.geo.client.helper.math.function.Function
import xyz.qweru.geo.client.helper.math.random.RandomProvider
import xyz.qweru.geo.core.Core
import xyz.qweru.geo.core.game.rotation.data.Rotation
import xyz.qweru.geo.core.game.rotation.processor.RotationProcessor

class FunctionRotationProcessor(private val function: Function) : RotationProcessor {

    override fun process(
        start: Rotation,
        end: Rotation,
        current: Rotation,
        delta: Rotation,
        random: RandomProvider
    ): Rotation {
        val length = start.deltaTo(end)
        val cur = start.deltaTo(current)

        val mulYaw = (cur.yaw / length.yaw + 1f) / 2f
        val mulPitch = ((cur.pitch / length.pitch + 1f) / 2f)
            .coerceIn(0f, 1f)

        val funYaw = if (mulYaw.isNaN()) 0f
                     else function.getAt(mulYaw)

        val funPitch = if (mulPitch.isNaN()) 0f
                       else function.getAt(mulPitch)

        Core.logger.info("")
        Core.logger.info("DELTA $delta !!!")
        Core.logger.info("YAW   start = ${start.yaw} current = ${current.yaw} cur = ${cur.yaw} end = ${end.yaw} length = ${length.yaw} fun = $funYaw")
        Core.logger.info("PITCH start = ${start.pitch} current = ${current.pitch} cur = ${cur.pitch} end = ${end.pitch} length = ${length.pitch} fun = $funPitch")

        return Rotation(
            delta.yaw * funYaw,
            delta.pitch * funPitch
        ).also {
            Core.logger.info("DELTA $it")
            Core.logger.info("")
        }
    }
}