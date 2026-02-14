package xyz.qweru.geo.core.system.setting

import xyz.qweru.geo.client.setting.BooleanSetting
import xyz.qweru.geo.client.setting.range.LongRangeSetting
import xyz.qweru.geo.client.setting.enums.EnumSetting
import xyz.qweru.geo.client.setting.enums.MultiEnumSetting
import xyz.qweru.geo.client.setting.number.FloatSetting
import xyz.qweru.geo.client.setting.number.IntSetting
import xyz.qweru.geo.client.setting.range.FloatRangeSetting
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation

// todo: maybe move setting storage to groups instead of the main setting object for easier display in the gui
// todo: don't `name.lowercase().replace(" ", "-")`
class SettingGroup(val name: String, val parent: Settings, private var visibleProvider: () -> Boolean = {true}
): ReadOnlyProperty<Any, SettingGroup>, PropertyDelegateProvider<Any, SettingGroup> {

    var usage: Array<out SettingUsage> = emptyArray()
        private set

    val visible: Boolean
        get() = visibleProvider.invoke()

    fun boolean(name: String, description: String, value: Boolean): BooleanSetting =
        BooleanSetting(name.lowercase().replace(" ", "-"), description, value, this).apply { parent.add(this) }

    fun float(name: String, description: String, value: Float, min: Float, max: Float): FloatSetting =
        FloatSetting(name.lowercase().replace(" ", "-"), description, value, this, min, max).apply { parent.add(this) }

    fun float(name: String, description: String, value: Float, bounds: ClosedFloatingPointRange<Float>): FloatSetting =
        FloatSetting(name.lowercase().replace(" ", "-"), description, value, this, bounds.start, bounds.endInclusive).apply { parent.add(this) }

    fun longRange(name: String, description: String, value: LongRange, minMax: LongRange): LongRangeSetting =
        LongRangeSetting(name.lowercase().replace(" ", "-"), description, value, minMax, this).apply { parent.add(this) }

    fun floatRange(name: String, description: String, value: ClosedFloatingPointRange<Float>, minMax: ClosedFloatingPointRange<Float>): FloatRangeSetting =
        FloatRangeSetting(name.lowercase().replace(" ", "-"), description, value, minMax, this).apply { parent.add(this) }

    fun int(name: String, description: String, value: Int, min: Int, max: Int): IntSetting =
        IntSetting(name.lowercase().replace(" ", "-"), description, value, this, min, max).apply { parent.add(this) }

    fun int(name: String, description: String, value: Int, bounds: IntRange): IntSetting =
        IntSetting(name.lowercase().replace(" ", "-"), description, value, this, bounds.first, bounds.last).apply { parent.add(this) }


    fun <T : Enum<*>> enum(name: String, description: String, value: T): EnumSetting<T> =
        EnumSetting(name.lowercase().replace(" ", "-"), description, value, this).apply { parent.add(this) }

    fun <T : Enum<T>> multiEnum(name: String, description: String, value: T, vararg values: T, enumConstants: Array<T>? = value.javaClass.enumConstants): MultiEnumSetting<T> =
        MultiEnumSetting(
            name.lowercase()
                .replace(" ", "-"),
            description,
            this,
            enumConstants,
            value,
            *values
        ).apply {
            parent.add(this)
        }

    fun visible(v: () -> Boolean): SettingGroup {
        visibleProvider = v
        return this
    }

    // required for @Usage to apply to entire groups

    override fun getValue(thisRef: Any, property: KProperty<*>): SettingGroup = this
    override operator fun provideDelegate(thisRef: Any, property: KProperty<*>): SettingGroup {
        val ann = property.findAnnotation<Usage>()
        usage = ann?.usage ?: emptyArray()
        return this
    }
}