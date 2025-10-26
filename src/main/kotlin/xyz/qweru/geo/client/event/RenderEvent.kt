package xyz.qweru.geo.client.event

import org.jetbrains.skija.Canvas

object GameRenderEvent
object WorldRenderEvent
object VanillaHudRenderEvent

// skija events
abstract class SkijaEvent {
    lateinit var canvas: Canvas
}
object FramebufferSizeChangeEvent {
    var width: Int = 1
    var height: Int = 1
}
object SwapBufferEvent
object UIRenderEvent : SkijaEvent()
object HudRenderEvent : SkijaEvent()