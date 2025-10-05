package xyz.qweru.geo.client.setting

import com.google.gson.JsonObject
import xyz.qweru.geo.core.setting.Setting
import xyz.qweru.geo.core.setting.SettingGroup

class IntSetting(name: String, description: String, default: Int, group: SettingGroup,
                 val min: Int, val max: Int)
    : Setting<IntSetting, Int>(name, description, default, group) {
    override fun save(jsonObject: JsonObject) {
        jsonObject.addProperty("value", value)
    }

    override fun load(jsonObject: JsonObject) {
        value = jsonObject.get("value").asInt
    }
}