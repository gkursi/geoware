package xyz.qweru.geo.core.setting

import com.google.gson.JsonObject
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
abstract class Setting<T : Setting<T, V>, V>(val name: String, val description: String, val default: V, val group: SettingGroup) {
    var changeListener: (V) -> Unit = {}
    var visibleProvider: () -> Boolean = {true}

    var value: V = default
        set(value) {
            field = value
            changeListener.invoke(value)
        }

    fun onChange(listener: (V) -> Unit): T {
        changeListener = listener
        return this as T
    }

    fun visible(provider: () -> Boolean): T {
        return this as T
    }

    operator fun getValue(u: Any?, property: KProperty<*>) = value
    operator fun setValue(u: Any?, property: KProperty<*>, v: V) {
        value = v
    }

    abstract fun save(jsonObject: JsonObject)
    abstract fun load(jsonObject: JsonObject)
}