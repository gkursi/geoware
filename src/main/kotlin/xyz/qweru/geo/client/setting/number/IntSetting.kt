package xyz.qweru.geo.client.setting.number

import com.google.gson.JsonObject
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import xyz.qweru.geo.core.system.setting.Setting
import xyz.qweru.geo.core.system.setting.SettingGroup
import java.util.concurrent.CompletableFuture

class IntSetting(name: String, description: String, default: Int, group: SettingGroup,
                 val min: Int, val max: Int)
    : Setting<IntSetting, Int>(name, description, default, group) {
    override fun <S> suggest(builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        builder.suggest(min.toString())
        builder.suggest(max.toString())
        builder.suggest(default.toString())
        return builder.buildFuture()
    }

    override fun parseAndSet(string: String) {
        value = string.toInt()
    }

    override fun save(jsonObject: JsonObject) {
        jsonObject.addProperty("value", value)
    }

    override fun load(jsonObject: JsonObject) {
        value = jsonObject.get("value").asInt
    }
}