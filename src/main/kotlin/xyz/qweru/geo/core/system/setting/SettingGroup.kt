package xyz.qweru.geo.core.system.setting

import xyz.qweru.geo.client.setting.BooleanSetting
import xyz.qweru.geo.client.setting.LongRangeSetting
import xyz.qweru.geo.client.setting.EnumSetting
import xyz.qweru.geo.client.setting.FloatSetting
import xyz.qweru.geo.client.setting.IntSetting

// todo: maybe move setting storage to groups instead of the main setting object for easier display in the gui
class SettingGroup(val name: String, val parent: Settings, private var visibleProvider: () -> Boolean = {true}) {
    val visible: Boolean
        get() = visibleProvider.invoke()

    fun boolean(name: String, description: String = "$name setting", value: Boolean): BooleanSetting =
        BooleanSetting(name.lowercase().replace(" ", "-"), description, value, this).apply { parent.add(this) }

    fun float(name: String, description: String = "$name setting", value: Float, min: Float, max: Float): FloatSetting =
        FloatSetting(name.lowercase().replace(" ", "-"), description, value, this, min, max).apply { parent.add(this) }

    fun longRange(name: String, description: String = "$name setting", value: LongRange, minMax: LongRange): LongRangeSetting =
        LongRangeSetting(name.lowercase().replace(" ", "-"), description, value, minMax, this).apply { parent.add(this) }

    fun int(name: String, description: String = "$name setting", value: Int, min: Int, max: Int): IntSetting =
        IntSetting(name.lowercase().replace(" ", "-"), description, value, this, min, max).apply { parent.add(this) }

    fun <T : Enum<*>> enum(name: String, description: String = "$name setting", value: T): EnumSetting<T> =
        EnumSetting(name.lowercase().replace(" ", "-"), description, value, this).apply { parent.add(this) }

    fun visible(v: () -> Boolean): SettingGroup {
        visibleProvider = v
        return this
    }
}