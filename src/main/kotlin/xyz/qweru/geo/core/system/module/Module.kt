package xyz.qweru.geo.core.system.module

import com.google.gson.JsonObject
import xyz.qweru.geo.core.Global
import xyz.qweru.geo.core.event.EventBus
import xyz.qweru.geo.core.system.setting.Settings
import xyz.qweru.geo.core.system.System

abstract class Module(name: String, val description: String = "${name.lowercase()} module", val category: Category = Category.MISC,
                      val alwaysEnabled: Boolean = false, var bind: Int = -1) : System(name, Type.MODULE) {
    protected val mc = Global.mc
    val settings = Settings(this)

    var enabled: Boolean = alwaysEnabled
        set(value) {
            val prev = field
            field = if (alwaysEnabled) true else value
            if (field != prev) {
                if (field) {
                    EventBus.subscribe(this)
                    println("Subscribed $name")
                    enable()
                }
                else {
                    EventBus.unsubscribe(this)
                    println("Unsubscribed $name")
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