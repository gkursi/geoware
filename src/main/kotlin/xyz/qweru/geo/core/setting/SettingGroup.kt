package xyz.qweru.geo.core.setting

import xyz.qweru.geo.client.setting.BooleanSetting
import xyz.qweru.geo.client.setting.DelaySetting
import xyz.qweru.geo.client.setting.EnumSetting
import xyz.qweru.geo.client.setting.FloatSetting
import xyz.qweru.geo.client.setting.IntSetting

class SettingGroup(val name: String, val parent: Settings, var visible: () -> Boolean = {true}) {
    fun boolean(name: String, description: String = "$name setting", value: Boolean): BooleanSetting =
        BooleanSetting(name, description, value, this).apply { parent.add(this) }
    fun float(name: String, description: String = "$name setting", value: Float, min: Float, max: Float): FloatSetting =
        FloatSetting(name, description, value, this, min, max).apply { parent.add(this) }
    fun delay(name: String, description: String = "$name setting", valueMin: Long, valueMax: Long, min: Long, max: Long): DelaySetting =
        DelaySetting(name, description, valueMin, valueMax, min, max, this).apply { parent.add(this) }
    fun int(name: String, description: String = "$name setting", value: Int, min: Int, max: Int): IntSetting =
        IntSetting(name, description, value, this, min, max).apply { parent.add(this) }
    fun <T : Enum<*>> enum(name: String, description: String = "$name setting", value: T): EnumSetting<T> =
        EnumSetting(name, description, value, this).apply { parent.add(this) }

    fun visible(v: () -> Boolean): SettingGroup {
        visible = v
        return this
    }
}