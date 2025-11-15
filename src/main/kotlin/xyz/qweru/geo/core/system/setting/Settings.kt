package xyz.qweru.geo.core.system.setting

import com.google.gson.JsonObject
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import xyz.qweru.geo.core.Global
import xyz.qweru.geo.core.system.System
import xyz.qweru.geo.core.helper.tree.SystemContext
import xyz.qweru.geo.core.system.module.Module

class Settings(val module: Module) : System("settings") {
    val allSettings = ObjectArrayList<Setting<*, *>>(5)

    fun group(name: String): SettingGroup = SettingGroup(name, this)

    fun add(setting: Setting<*, *>) {
        allSettings.add(setting)
        logger.info("Added setting ${setting.name} with value ${setting.value} in group ${setting.group.name} (${allSettings.size} total)")
    }

    override fun loadThis(json: JsonObject) {
        for (setting in allSettings)
            json[setting.name]?.let { setting.load(it.asJsonObject) } ?: {
                Global.logger.info("$name has no value in $json")
            }
    }

    override fun save(json: JsonObject, ctx: SystemContext) {
        for (setting in allSettings) {
            if (!(ctx.settingFilter?.invoke(module, setting) ?: false))
                continue
            val obj = JsonObject()
            setting.save(obj)
            json.add(setting.name, obj)
        }
    }

    override fun saveThis(json: JsonObject) = throw AssertionError()

    override fun initThis() {}
}

