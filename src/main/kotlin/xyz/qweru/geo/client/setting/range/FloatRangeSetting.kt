package xyz.qweru.geo.client.setting.range

import com.google.gson.JsonObject
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import xyz.qweru.geo.client.helper.math.RangeHelper
import xyz.qweru.geo.core.system.setting.Setting
import xyz.qweru.geo.core.system.setting.SettingGroup
import java.util.concurrent.CompletableFuture

class FloatRangeSetting(name: String, description: String, default: ClosedRange<Float>, val minMax: ClosedRange<Float>, group: SettingGroup
) : Setting<FloatRangeSetting, ClosedRange<Float>>(name, description, default, group) {
    override fun <S> suggest(builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        builder.suggest("${value.start} ${value.endInclusive}")
        builder.suggest("${default.start} ${default.endInclusive}")
        builder.suggest("${minMax.start} ${minMax.endInclusive}")
        return builder.buildFuture()
    }

    override fun parseAndSet(string: String) {
        val parts = string.split(" ")
        if (parts.size != 2) throw Exception()
        value = RangeHelper.rangeOf(parts[0].toFloat(), parts[1].toFloat())
    }

    override fun save(jsonObject: JsonObject) {
        jsonObject.addProperty("min", value.start)
        jsonObject.addProperty("max", value.endInclusive)
    }

    override fun load(jsonObject: JsonObject) {
        value = RangeHelper.rangeOf(jsonObject.get("min").asFloat, jsonObject.get("max").asFloat)
    }
}