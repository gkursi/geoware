package xyz.qweru.geo.core.game.rotation

interface InterpolationEngine {
    fun step(start: Float, end: Float): Float
    fun onYawDelta(delta: Float) {}
    fun onPitchDelta(delta: Float) {}
}