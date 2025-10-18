package xyz.qweru.geo.client.setting

import com.google.gson.JsonObject
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import xyz.qweru.geo.core.system.setting.Setting
import xyz.qweru.geo.core.system.setting.SettingGroup
import java.util.concurrent.CompletableFuture

class EnumSetting<T : Enum<*>>(name: String, description: String, default: T, group: SettingGroup
) : Setting<EnumSetting<T>, T>(name, description, default, group) {

    val constants = default.javaClass.enumConstants!!
    val displayNames = constants.associateWith { it.name.replace("_", " ").lowercase().replaceFirstChar { c -> c.uppercase() } }
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

    override fun <S> suggest(builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        val input = builder.remaining
        for (name in displayNames.values) {
            if (name.lowercase().startsWith(input)) builder.suggest(name)
        }
        return builder.buildFuture()
    }

    override fun parseAndSet(string: String) {
        constants.forEachIndexed { i, it ->
            if (it.name == string) {
                value = it
                index = i
                return
            }
        }
        throw IllegalArgumentException()
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