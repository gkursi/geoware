package xyz.qweru.geo.client.setting.number

import com.google.gson.JsonObject
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import xyz.qweru.geo.core.helper.file.set
import xyz.qweru.geo.core.system.setting.Setting
import xyz.qweru.geo.core.system.setting.SettingGroup
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KProperty

class FloatSetting(name: String, description: String, default: Float, group: SettingGroup,
                   val min: Float, val max: Float)
    : Setting<FloatSetting, Float>(name, description, default, group) {
    override fun <S> suggest(builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        builder.suggest(min.toString())
        builder.suggest(max.toString())
        builder.suggest(default.toString())
        return builder.buildFuture()
    }

    override fun parseAndSet(string: String) {
        value = string.toFloat()
    }

    override fun save(jsonObject: JsonObject) {
        jsonObject["value"] = value
    }

    override fun load(jsonObject: JsonObject) {
        value = jsonObject["value"].asFloat
    }

}