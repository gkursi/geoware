package xyz.qweru.geo.core.event

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Handler(val priority: Int = EventPriority.NONE)
