package xyz.qweru.geo.core.system

import com.google.gson.JsonObject
import it.unimi.dsi.fastutil.objects.ReferenceCollection
import xyz.qweru.geo.core.system.friend.Friends
import xyz.qweru.geo.core.system.module.Modules
import kotlin.reflect.KClass

/**
 * Parent class for all systems
 */
class Systems : System {
    private constructor() : super("systems")

    companion object {
        val INSTANCE = Systems()

        fun init() = INSTANCE.init()
        fun getSystems(): ReferenceCollection<System> = INSTANCE.getSubsystems()
        @Suppress("UNCHECKED_CAST")
        fun <T : System> get(system: KClass<T>): T = INSTANCE.get(system)
        fun <T : System> get(system: Class<T>): T = INSTANCE.get(system)
    }

    override fun init() {
        super.init()
        Walker.walk(this)
        val size = getSubsystems().size
        logger.info("${if(firstInit) "L" else "Rel"}oaded $size system${if (size == 1) "" else "s"} (${Walker.size - size} subsystems)")
    }

    override fun initThis() {
        add(Modules())
        add(Friends())
    }

    override fun loadThis(json: JsonObject) = throw AssertionError()
    override fun saveThis(json: JsonObject) = throw AssertionError()
}