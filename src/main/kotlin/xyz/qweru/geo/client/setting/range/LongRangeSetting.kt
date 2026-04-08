package xyz.qweru.geo.client.setting.range

import com.google.gson.JsonObject
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import xyz.qweru.geo.core.system.impl.setting.Setting
import xyz.qweru.geo.core.system.impl.setting.SettingGroup
import java.util.concurrent.CompletableFuture

class LongRangeSetting(name: String, description: String, default: LongRange, val minMax: LongRange, group: SettingGroup) : Setting<LongRangeSetting, LongRange>(name, description,
    default, group) {
    override fun <S> suggest(builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        builder.suggest("${default.first} ${default.last}")
        builder.suggest("${minMax.first} ${minMax.last}")
        return builder.buildFuture()
    }

    override fun parseAndSet(string: String) {
        val parts = string.split(' ')
        if (parts.size != 2) {
            throw Exception()
        }
        value = LongRange(parts[0].toLong(), parts[1].toLong())
    }

    override fun save(jsonObject: JsonObject) {
        jsonObject.addProperty("min", value.first)
        jsonObject.addProperty("max", value.last)
    }

    override fun load(jsonObject: JsonObject) {
        value = LongRange(
            jsonObject["min"].asLong,
            jsonObject["max"].asLong
        )
    }
}