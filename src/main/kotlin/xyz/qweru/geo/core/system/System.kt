package xyz.qweru.geo.core.system

import com.google.gson.JsonObject
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap
import it.unimi.dsi.fastutil.objects.ReferenceCollection
import xyz.qweru.geo.core.Glob
import xyz.qweru.geo.core.event.Events
import kotlin.reflect.KClass

abstract class System(val name: String) {
    private val sub: Reference2ReferenceOpenHashMap<KClass<out System>, System> = Reference2ReferenceOpenHashMap()

    protected var firstInit = true
        private set
    protected val logger = Glob.logger

    @Suppress("UNCHECKED_CAST")
    fun <T : System> get(system: KClass<T>): T {
        return sub[system]!! as T
    }

    fun <T : System> get(system: Class<T>): T {
        return get(system.kotlin)
    }

    open fun add(system: System) {
        sub[system::class] = system
        system.init()
    }

    /**
     * @param stateProvider lets you selectively load subsystems
     */
    open fun load(json: JsonObject, stateProvider: (System, System) -> Boolean = {_, _ -> true}) {
        loadThis(json)
        for (system in sub.values) {
            if (!stateProvider.invoke(this, system)) continue
            json[system.name]?.let { system.load(it as JsonObject, stateProvider) }
        }
    }

    /**
     * @param stateProvider lets you selectively save subsystems
     */
    open fun save(json: JsonObject, stateProvider: (System, System) -> Boolean = {_, _ -> true}) {
        saveThis(json)
        for (system in sub.values) {
            if (!stateProvider.invoke(this, system)) continue
            json[system.name]?.let { system.save(it as JsonObject, stateProvider) }
        }
    }

    fun getSubsystems(): ReferenceCollection<System> = sub.values

    open fun init() {
        sub.clear()
        initThis()
        if (firstInit) Events.subscribe(this)
        firstInit = false
    }

    protected abstract fun initThis()
    protected abstract fun loadThis(json: JsonObject)
    protected abstract fun saveThis(json: JsonObject)
}