package xyz.qweru.geo.client.setting

import com.google.gson.JsonObject
import xyz.qweru.geo.core.system.setting.Setting
import xyz.qweru.geo.core.system.setting.SettingGroup

class LongRangeSetting(name: String, description: String, default: LongRange, val minMax: LongRange, group: SettingGroup) : Setting<LongRangeSetting, LongRange>(name, description,
    default, group) {
    override fun save(jsonObject: JsonObject) {
        jsonObject.addProperty("min", value.start)
        jsonObject.addProperty("max", value.endInclusive)
    }

    override fun load(jsonObject: JsonObject) {
        value = LongRange(jsonObject.get("min").asLong, jsonObject.get("max").asLong)
    }
}