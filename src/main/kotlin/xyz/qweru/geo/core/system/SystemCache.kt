package xyz.qweru.geo.core.system

import com.mojang.blaze3d.systems.RenderSystem
import xyz.qweru.geo.core.config.Configs
import xyz.qweru.geo.core.system.impl.module.Module
import xyz.qweru.geo.core.system.impl.module.Modules
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

object SystemCache {
    val currentSync
        get() = Configs.syncId

    inline fun <reified T : Module> getModule(): Cached<T> =
        Cached(T::class, { Systems.get(Modules::class) }, null, currentSync)

    inline fun <reified T : System> get(): Cached<T> =
        Cached(T::class, null, null, currentSync)

    // for java
    fun <T : System> getModule(clazz: Class<T>): Cached<T> =
        Cached(clazz.kotlin, { Systems.get(Modules::class) }, null, currentSync)

    private fun syncCacheIfNeeded(cached: Cached<*>) {
        if (cached.system != null && (!RenderSystem.isOnRenderThread() || cached.lastSync == currentSync)) return
        val parent = cached.parent?.invoke() ?: Systems

        cached.lastSync = currentSync
        cached.system = parent.get(cached.klass)
    }

    @Suppress("UNCHECKED_CAST")
    data class Cached<T : System>(val klass: KClass<out System>, val parent: (() -> System)?, var system: System?, var lastSync: Long = 0) {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            syncCacheIfNeeded(this)
            return system!! as T
        }

        // for java
        fun cast(): T {
            syncCacheIfNeeded(this)
            return system!! as T
        }
    }
}