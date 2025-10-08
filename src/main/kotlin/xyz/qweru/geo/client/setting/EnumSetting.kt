package xyz.qweru.geo.client.setting

import com.google.gson.JsonObject
import xyz.qweru.geo.core.setting.Setting
import xyz.qweru.geo.core.setting.SettingGroup

class EnumSetting<T : Enum<*>>(name: String, description: String, default: T, group: SettingGroup
) : Setting<EnumSetting<T>, T>(name, description, default, group) {

    val constants = default.javaClass.enumConstants!!
    val displayNames = constants.associateWith { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } }
    private var index = 0

    init {
        constants.forEachIndexed { i, it ->
            if (it == default) {
                index = i
            }
        }
    }

    override fun save(jsonObject: JsonObject) {
        jsonObject.addProperty("value", value.name)
    }

    override fun load(jsonObject: JsonObject) {
        val name = jsonObject.get("value").asString
        constants.forEachIndexed { i, it ->
            if (it.name == name) {
                value = it
                index = i
                return
            }
        }
    }

    fun next() {
        index += 1
        index %= constants.size
    }

    fun prev() {
        index -= 1
        index %= constants.size
    }
}