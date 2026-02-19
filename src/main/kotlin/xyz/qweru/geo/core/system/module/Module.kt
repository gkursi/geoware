package xyz.qweru.geo.core.system.module

import com.google.gson.JsonObject
import xyz.qweru.geo.core.Core
import xyz.qweru.geo.core.event.EventBus
import xyz.qweru.geo.core.system.setting.Settings
import xyz.qweru.geo.core.system.System
import xyz.qweru.geo.core.ui.notification.Notifications
import xyz.qweru.geo.extend.kotlin.log.dbg

abstract class Module(
    name: String,
    val description: String = "${name.lowercase()} module",
    val category: Category = Category.MISC,
    val alwaysEnabled: Boolean = false,
    var bind: Int = -1
) : System(name, Type.MODULE) {
    protected val mc = Core.mc
    val settings = Settings(this)

    var enabled: Boolean = alwaysEnabled
        set(value) {
            val prev = field
            field = if (alwaysEnabled) true else value
            if (field != prev) {
                Notifications.onToggle(this)
                if (field) {
                    EventBus.subscribe(this)
                    enable()
                } else {
                    EventBus.unsubscribe(this)
                    disable()
                }
            }
        }
    val inGame: Boolean get() = mc.level != null && mc.player != null

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