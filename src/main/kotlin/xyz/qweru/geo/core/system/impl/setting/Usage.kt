package xyz.qweru.geo.core.system.impl.setting

/**
 * Indicates how a setting/group is used for partial config exporting
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Usage(vararg val usage: SettingUsage)

enum class SettingUsage {
    VISUAL, COLOR, COLOR_PRIMARY, COLOR_SECONDARY, POSITION
}