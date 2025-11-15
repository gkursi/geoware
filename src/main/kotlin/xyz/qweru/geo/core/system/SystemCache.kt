package xyz.qweru.geo.core.system

import com.mojang.blaze3d.systems.RenderSystem
import xyz.qweru.geo.core.system.config.Configs
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.core.system.module.Modules
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

object SystemCache {
    val currentSync
        get() = Configs.Companion.changes

    inline fun <reified T : Module> getModule(): Cached<T> {
        val klass = T::class
        val module = Systems.get(Modules::class).get(klass)
        return Cached(klass, module, currentSync)
    }

    inline fun <reified T : System> get(): Cached<T> {
        val klass = T::class
        val system = Systems.get(klass)
        return Cached(klass, system, currentSync)
    }

    private fun syncCacheIfNeeded(cached: Cached<*>) {
        if (!RenderSystem.isOnRenderThread() || cached.lastSync == currentSync) return
        cached.lastSync = currentSync
        cached.system = Systems.get(Modules::class).get(cached.klass)
    }

    data class Cached<T : System>(val klass: KClass<out System>, var system: System, var lastSync: Long = 0) {
        @Suppress("UNCHECKED_CAST")
        operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            syncCacheIfNeeded(this)
            return system as T
        }
    }
}