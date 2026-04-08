package xyz.qweru.geo.core.system.impl.setting

import com.google.gson.JsonObject
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import xyz.qweru.geo.core.system.System
import xyz.qweru.geo.core.helper.tree.SystemContext
import xyz.qweru.geo.core.system.impl.module.Module
import xyz.qweru.geo.extend.kotlin.log.dbg

class Settings(val module: Module) : System("settings") {
    val allSettings = ObjectArrayList<Setting<*, *>>(5)
    val general = group("General")

    fun group(name: String): SettingGroup = SettingGroup(name, this)

    fun add(setting: Setting<*, *>) {
        allSettings.add(setting)
        logger.dbg("Added setting ${setting.name} with value ${setting.value} in group ${setting.group.name} (${allSettings.size} total)")
    }

    override fun loadThis(json: JsonObject) {
        for (setting in allSettings)
            json[setting.name]?.let { setting.load(it.asJsonObject) }
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

