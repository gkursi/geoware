package xyz.qweru.geo.client.setting

import com.google.gson.JsonObject
import xyz.qweru.geo.core.setting.Setting
import xyz.qweru.geo.core.setting.SettingGroup

class DelaySetting(name: String, description: String, defaultMin: Long, defaultMax: Long, val min: Long, val max: Long, group: SettingGroup) : Setting<DelaySetting, DelaySetting.Delay>(name, description,
    Delay(defaultMin, defaultMax), group) {
    override fun save(jsonObject: JsonObject) {
        jsonObject.addProperty("min", value.min)
        jsonObject.addProperty("max", value.max)
    }

    override fun load(jsonObject: JsonObject) {
        value.min = jsonObject.get("min").asLong
        value.max = jsonObject.get("max").asLong
    }

    data class Delay(var min: Long, var max: Long)
}