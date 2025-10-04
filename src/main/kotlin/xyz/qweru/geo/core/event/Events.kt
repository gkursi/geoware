package xyz.qweru.geo.core.event

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap
import xyz.qweru.geo.core.Glob
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.javaMethod

object Events {
    /** Event type references are stored as java classes, because the hashes of KClasses
        sourced from object::class and the KClasses sourced from reflection differ       */
    private val handlerCache = ConcurrentHashMap<KClass<out Any>, Reference2ReferenceOpenHashMap<Class<out Any>, MethodHandle>>()
    private val handlers = ConcurrentHashMap<Class<out Any>, ObjectOpenHashSet<HandlerInfo>>()

    fun subscribe(obj: Any) {
        scan(obj)
        handlerCache[obj::class]?.forEach { event, handler ->
            handlers.computeIfAbsent(event) { ObjectOpenHashSet() }.add(HandlerInfo(obj, handler))
        }
    }

    fun unsubscribe(obj: Any) {
        for (infos in handlers.values) {
            infos.removeIf { it.instance == obj }
        }
    }

    fun scan(obj: Any) {
        if (handlerCache.contains(obj::class)) return
        val map = Reference2ReferenceOpenHashMap<Class<out Any>, MethodHandle>()
        handlerCache.put(obj::class, map)

        for (function in obj::class.declaredMemberFunctions) {
            if (function.findAnnotation<Handler>() != null) {
                if (function.parameters.size != 2) throw IllegalArgumentException("Handler method ${function.name} can only have 1 parameter (has ${function.parameters.size - 1})")
                val kClass = function.parameters[1].type.classifier!! as KClass<*>
                map.put(kClass.java, unwrap(function))
                Glob.logger.info("Mapped ${function.name} to event type ${kClass.simpleName}")
            }
        }
    }

    fun <T> post(event: T): T {
        check(event != null) { "Event cannot be null" }
        handlers[event::class.java]?.forEach { info -> info.func.invoke(info.instance, event) }
        return event
    }

    fun <T : Cancellable> post(event: T): T {
        check(true) { "Event cannot be null" }
        handlers[event::class.java]?.let {
            for (info in it) {
                info.func.invoke(info.instance, event)
                if (event.cancelled) break
            }
        }
        return event
    }

    private data class HandlerInfo(val instance: Any, val func: MethodHandle)

    private fun unwrap(kFunction: KFunction<*>): MethodHandle {
        return MethodHandles.lookup().unreflect(kFunction.javaMethod!!.also { it.isAccessible = true })
    }

}