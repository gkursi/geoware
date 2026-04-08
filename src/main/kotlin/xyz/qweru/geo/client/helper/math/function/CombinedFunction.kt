package xyz.qweru.geo.client.helper.math.function

import xyz.qweru.geo.core.Core

/**
 * @param sizes should add up to 1
 */
class CombinedFunction(functions: Array<Function>, sizes: FloatArray) : Function {
    private val curves: List<CurveSegment>

    init {
        var offset = 0.0
        this.curves = functions.mapIndexed { i, curve ->
            val size = sizes[i]
            val segment = CurveSegment(
                offset,
                offset + size,
                curve
            )

            offset += size
            return@mapIndexed segment
        }
    }

    override fun getAt(x: Double): Double {
        val index = curves.binarySearch {
            if (it.contains(x)) {
                return@binarySearch 0
            }

            return@binarySearch if (it.from < x) {
                -1
            } else {
                1
            }
        }

        val segment by lazy { curves[index.coerceIn(0, curves.size - 1)] }

        if (!segment.contains(x)) {
            throw IllegalStateException("Value $x oob for curve $segment")
        }

        Core.logger.info("SEG   $segment")

        return segment.function.getAt(
            segment.from + ((x - segment.from) / segment.to) * (segment.to - segment.from)
        )
    }

    private data class CurveSegment(val from: Double, val to: Double, val function: Function) {
        private val range = from..to

        fun contains(value: Double) =
            value in range

        override fun toString(): String {
            return "CurveSegment($from..$to)"
        }
    }
}