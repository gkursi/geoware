package xyz.qweru.geo.core.system.setting

import com.google.gson.JsonObject
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import org.apache.commons.lang3.text.WordUtils
import xyz.qweru.geo.core.Global
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation

@Suppress("UNCHECKED_CAST")
abstract class Setting<T : Setting<T, V>, V>(val name: String, val description: String, val default: V, val group: SettingGroup) {
    private var changeListener: (T) -> Unit = {}
    private var visibleProvider: () -> Boolean = {true}
    private lateinit var usage: Array<SettingUsage> // TODO: maybe replace with a (Hash)Set
    val displayName = WordUtils.capitalize(name.replace("_", " "))

    val visible: Boolean
        get() = visibleProvider.invoke()

    var value: V = default
        set(value) {
            field = value
            changeListener.invoke(this as T)
        }

    fun onChange(listener: (T) -> Unit): T {
        changeListener = listener
        return this as T
    }

    fun visible(provider: () -> Boolean): T {
        visibleProvider = provider
        return this as T
    }

    fun hasUsage(usage: SettingUsage): Boolean = this.usage.contains(usage)

    abstract fun <S> suggest(builder: SuggestionsBuilder): CompletableFuture<Suggestions>

    /**
     * Must throw IllegalArgumentException if parsing failed
     */
    abstract fun parseAndSet(string: String)

    operator fun getValue(u: Any?, property: KProperty<*>) = value
    operator fun setValue(u: Any?, property: KProperty<*>, v: V) {
        value = v
    }

    operator fun provideDelegate(u: Any?, property: KProperty<*>): Setting<T, V> {
        val ann = property.findAnnotation<Usage>()
        usage = ((ann?.usage ?: emptyArray()) as Array<SettingUsage>) + group.usage
        Global.logger.info("Setting usage: ${usage.joinToString(", ")}")
        return this
    }

    abstract fun save(jsonObject: JsonObject)
    abstract fun load(jsonObject: JsonObject)
}