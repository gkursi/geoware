package xyz.qweru.geo.client.helper.math.function

import org.apache.commons.math3.analysis.function.Logistic
import xyz.qweru.geo.client.helper.math.random.LayeredRandom

class SigmoidFunction : Function {
    private val curve = Logistic.Parametric()

    val parameters = doubleArrayOf(
        1.0, // k
        1.0, // m
        LayeredRandom.DEFAULT.double(
            0.1, 1.0 // b
        ),
        1.0, // q,
        0.0, // a
        1.0  // n
    )

    override fun getAt(x: Double): Double =
        curve.value(
            x,
            *parameters
        )
}