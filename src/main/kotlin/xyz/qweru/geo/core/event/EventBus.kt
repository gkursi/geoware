package xyz.qweru.geo.core.event

import xyz.qweru.basalt.Cancellable
import xyz.qweru.basalt.EventBus

/**
 * Global bus instance
 */
object EventBus {
    private val bus: EventBus = EventBus()

    fun <T> post(event: T) = bus.post(event)
    fun <T : Cancellable> post(event: T) = bus.post(event)

    fun subscribe(obj: Any) = bus.subscribe(obj)
    fun unsubscribe(obj: Any) = bus.unsubscribe(obj)
}