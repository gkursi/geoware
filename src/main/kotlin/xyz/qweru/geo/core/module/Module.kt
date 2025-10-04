package xyz.qweru.geo.core.module

import com.google.gson.JsonObject
import xyz.qweru.geo.core.Glob
import xyz.qweru.geo.core.event.Events
import xyz.qweru.geo.core.setting.Settings
import xyz.qweru.geo.core.system.System

abstract class Module(name: String, val description: String = "$name module", val category: Category = Category.MISC,
                      val alwaysEnabled: Boolean = false) : System(name) {
    protected val mc = Glob.mc
    protected val settings = Settings()

    var enabled: Boolean = alwaysEnabled
        set(value) {
            val prev = field
            field = if (alwaysEnabled) true else value
            if (field != prev) {
                if (field) {
                    Events.subscribe(this)
                    enable()
                }
                else {
                    Events.unsubscribe(this)
                    disable()
                }
            }
        }
    val inGame: Boolean get() = mc.world != null && mc.player != null

    protected open fun enable() {}
    protected open fun disable() {}

    override fun saveThis(json: JsonObject) {
        json.addProperty("enabled", enabled)
    }

    override fun loadThis(json: JsonObject) {
        json["enabled"]?.let { enabled = it.asBoolean }
    }

    override fun initThis() {
        add(settings)
    }
}