package xyz.qweru.geo.client.setting

import com.google.common.primitives.Booleans
import com.google.gson.JsonObject
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import xyz.qweru.geo.core.system.setting.Setting
import xyz.qweru.geo.core.system.setting.SettingGroup
import java.util.concurrent.CompletableFuture

class BooleanSetting(name: String, description: String, default: Boolean, group: SettingGroup) : Setting<BooleanSetting, Boolean>(
    name,
    description,
    default,
    group
) {
    override fun <S> suggest(builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        if ("true".startsWith(builder.remaining)) builder.suggest("true")
        if ("false".startsWith(builder.remaining)) builder.suggest("false")
        return builder.buildFuture()
    }

    override fun parseAndSet(string: String) {
        value = string.toBooleanStrict()
    }

    override fun save(jsonObject: JsonObject) {
        jsonObject.addProperty("value", value)
    }

    override fun load(jsonObject: JsonObject) {
        value = jsonObject.get("value").asBoolean
    }
}