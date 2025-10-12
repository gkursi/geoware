package xyz.qweru.geo.core.system.setting

import com.google.gson.JsonObject
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import xyz.qweru.geo.core.system.System

class Settings : System("settings") {
    val settings = ObjectArrayList<Setting<*, *>>(5)

    fun group(name: String): SettingGroup = SettingGroup(name, this)

    fun add(setting: Setting<*, *>) {
        settings.add(setting)
        logger.info("Added setting ${setting.name} with value ${setting.value} in group ${setting.group.name} (${settings.size} total)")
    }

    override fun loadThis(json: JsonObject) {
        for (setting in settings)
            json[setting.name]?.let { setting.load(it.asJsonObject) }
    }

    override fun saveThis(json: JsonObject) {
        for (setting in settings) {
            val obj = JsonObject()
            setting.save(obj)
            json.add(setting.name, obj)
        }
    }

    override fun initThis() {}
}

