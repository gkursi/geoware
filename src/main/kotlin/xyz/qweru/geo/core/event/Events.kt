package xyz.qweru.geo.core.event

import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap
import it.unimi.dsi.fastutil.objects.ReferenceArrayList
import it.unimi.dsi.fastutil.objects.ReferenceImmutableList
import it.unimi.dsi.fastutil.objects.ReferenceList
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.javaMethod

object Events {
    /**
     * Class containing handlers to a map of event classes to a list of their respective handlers \
     * Event type references are stored as java classes, because the hashes of KClasses
     * sourced from object::class and the KClasses sourced from reflection differ
     */
    private val handlerCache = ConcurrentHashMap<KClass<out Any>, Reference2ReferenceOpenHashMap<Class<out Any>, ObjectArrayList<Handle>>>()
    private val handles = ConcurrentHashMap<Class<out Any>, CopyOnWriteArrayList<Handle>>()

    /**
     * Currently you cannot subscribe multiple instances of a class at the same time.
     * This can be fixed by caching the `MethodHandle` directly, but this requires
     * instantiation of a `Handle` on every subscribe, which is slower and currently not needed.
     */
    fun subscribe(obj: Any) {
        scan(obj)
        handlerCache[obj::class]?.forEach { event, handlersInObj ->
            val eventHandles = getHandles(event)
            handlersInObj.forEach { addHandle(obj, it, eventHandles) }
        }
    }

    private fun addHandle(obj: Any, thisHandle: Handle, list: CopyOnWriteArrayList<Handle>) {
        thisHandle.instance = obj
        var i = 0
        for (handle in list) {
            if (handle.priority < thisHandle.priority) break
            i++
        }
        list.add(i, thisHandle)
    }

    private fun getHandles(event: Class<out Any>) = handles.computeIfAbsent(event) { CopyOnWriteArrayList() }

    fun unsubscribe(obj: Any) {
        for (infos in handles.values) {
            // replacing this with a binary search based on the priority might be faster
            infos.removeIf { it.instance == obj }
        }
    }

    fun scan(obj: Any) {
        if (handlerCache.contains(obj::class)) return
        val cache = Reference2ReferenceOpenHashMap<Class<out Any>, ObjectArrayList<Handle>>()

        for (function in obj::class.declaredMemberFunctions) {
            val tag = function.findAnnotation<Handler>()
            if (tag == null) continue
            if (function.parameters.size != 2)
                throw IllegalArgumentException(
                    "Handler method ${function.name}" +
                        " can only have 1 parameter" +
                        " (has ${function.parameters.size - 1})")

            val kClass = function.parameters[1].type.classifier!! as KClass<*>
            cache.computeIfAbsent(kClass.java) { ObjectArrayList() }
                .add(Handle(null, unwrap(function), tag.priority))
        }

        if (!cache.isEmpty()) handlerCache.put(obj::class, cache)
    }

    fun <T> post(event: T): T {
        check(event != null) { "Event cannot be null" }
        handles[event::class.java]?.forEach { info -> info.func.invoke(info.instance, event) }
        return event
    }

    fun <T : Cancellable> post(event: T): T {
        check(true) { "Event cannot be null" }
        handles[event::class.java]?.let {
            for (info in it) {
                info.func.invoke(info.instance, event)
                if (event.cancelled) break
            }
        }
        return event
    }

    private data class Handle(var instance: Any?, val func: MethodHandle, val priority: Int)

    private fun unwrap(kFunction: KFunction<*>): MethodHandle {
        return MethodHandles.lookup().unreflect(kFunction.javaMethod!!.also { it.isAccessible = true })
    }

}