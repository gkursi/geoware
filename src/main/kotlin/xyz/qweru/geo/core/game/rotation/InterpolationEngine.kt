package xyz.qweru.geo.core.game.rotation

interface InterpolationEngine {
    fun stepYaw(start: Float, end: Float, current: Float): Float
    fun stepPitch(start: Float, end: Float, current: Float): Float

    fun onYawDelta(delta: Float) {}
    fun onPitchDelta(delta: Float) {}
}