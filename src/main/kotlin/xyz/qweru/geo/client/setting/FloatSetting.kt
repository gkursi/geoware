package xyz.qweru.geo.client.setting

import com.google.gson.JsonObject
import xyz.qweru.geo.core.setting.Setting
import xyz.qweru.geo.core.setting.SettingGroup

class FloatSetting(name: String, description: String, default: Float, group: SettingGroup,
                   val min: Float, val max: Float)
    : Setting<FloatSetting, Float>(name, description, default, group) {
    override fun save(jsonObject: JsonObject) {
        value = jsonObject.get("value").asFloat
    }

    override fun load(jsonObject: JsonObject) {
        jsonObject.addProperty("value", value)
    }
}