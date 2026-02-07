package xyz.qweru.geo.client.setting.enums

import com.google.common.collect.ImmutableSet
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import xyz.qweru.geo.core.system.setting.Setting
import xyz.qweru.geo.core.system.setting.SettingGroup
import xyz.qweru.geo.extend.kotlin.array.withModification
import java.util.concurrent.CompletableFuture

class MultiEnumSetting<T : Enum<T>>(name: String, description: String, group: SettingGroup, default: T, vararg defaults: T
) : Setting<MultiEnumSetting<T>, MultiEnumSetting.MultiEnumChoice<T>>(name, description, MultiEnumChoice(default, *defaults), group) {

    val constants = default.javaClass.enumConstants!!
    val displayNames = constants.associateWith { it.name.replace("_", " ").lowercase().replaceFirstChar { c -> c.uppercase() } }

    private val suggestions = displayNames.values.let { arr ->
        arr.withModification { "+$it" } + arr.withModification { "-$it" }
    }

    private var index = 0

    init {
        constants.forEachIndexed { i, it ->
            if (it == default) {
                index = i
            }
        }
    }

    override fun save(jsonObject: JsonObject) {
        val arr = JsonArray()
        for (ts in value.getEnabled())
            arr.add(ts.name)
        jsonObject.add("enabled", arr)
    }

    override fun load(jsonObject: JsonObject) {
        value.clear()
        val enabled = jsonObject["enabled"].asJsonArray
        for (ts in enabled) {
            val name = ts.asString
            value.add(constants.find { it.name == name } ?: continue)
        }
    }

    override fun <S> suggest(builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        val input = builder.remaining.lowercase()
        for (name in suggestions) {
            if (name!!.lowercase().startsWith(input)) builder.suggest(name)
        }
        return builder.buildFuture()
    }

    override fun parseAndSet(string: String) {
        val str = string.replace(" ", "_").uppercase().substring(1)
        val action = string[0]

        if (action != '+' && action != '-')
            throw IllegalArgumentException()

        constants.forEachIndexed { i, it ->
            if (it.name == str) {
                when (action) {
                    '+' -> value.add(it)
                    '-' -> value.remove(it)
                    else -> throw IllegalStateException()
                }
                return
            }
        }

        throw IllegalArgumentException()
    }

    /**
     * wrapper for slightly cleaner syntax
     */
    class MultiEnumChoice<T : Enum<T>>(vararg default: T) {
        private val enabled = ObjectOpenHashSet<T>()
            .also {
                it.addAll(default)
            }

        fun has(t: T) = enabled.contains(t)
        fun add(t: T) = enabled.add(t)
        fun remove(t: T) = enabled.remove(t)
        fun getEnabled() = enabled
        fun clear() = enabled.clear()
    }
}