package xyz.qweru.geo.core.system

import com.google.gson.JsonObject
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap
import it.unimi.dsi.fastutil.objects.ReferenceCollection
import xyz.qweru.geo.core.Global
import xyz.qweru.geo.core.helper.tree.SystemContext
import kotlin.reflect.KClass

/**
 * @param name must be lowercase
 * @param type indicator for config options
 */
abstract class System(open val name: String, val type: Type = Type.INTERNAL) {
    private val sub: Reference2ReferenceOpenHashMap<KClass<out System>, System> = Reference2ReferenceOpenHashMap()

    protected var firstInit = true
        private set
    protected val logger = Global.logger

    @Suppress("UNCHECKED_CAST")
    fun <T : System> get(system: KClass<T>): T = sub[system]!! as T
    fun <T : System> get(system: Class<T>): T = get(system.kotlin)

    open fun add(system: System) {
        sub[system::class] = system
        system.init()
    }

    /**
     * @param ctx lets you selectively load subsystems
     */
    open fun load(json: JsonObject, ctx: SystemContext = SystemContext()) {
        loadThis(json)
        for (system in sub.values) {
            if (!(ctx.systemFilter?.invoke(this, system) ?: false)) continue
            json[system.name]?.let { system.load(it as JsonObject, ctx) }
        }
    }

    /**
     * @param ctx lets you selectively save subsystems
     */
    open fun save(json: JsonObject, ctx: SystemContext = SystemContext()) {
        saveThis(json)
        for (system in sub.values) {
            if (system == null) throw IllegalStateException("System is null!")
            if (!(ctx.systemFilter?.invoke(this, system) ?: false)) continue
            val obj = JsonObject()
            system.save(obj, ctx)
            json.add(system.name, obj)
        }
    }

    fun getSubsystems(): ReferenceCollection<System> = sub.values

    open fun init() {
        sub.clear()
        initThis()
        firstInit = false
    }

    protected abstract fun initThis()
    protected abstract fun loadThis(json: JsonObject)
    protected abstract fun saveThis(json: JsonObject)

    enum class Type {
        ROOT, MODULE, INTERNAL
    }
}