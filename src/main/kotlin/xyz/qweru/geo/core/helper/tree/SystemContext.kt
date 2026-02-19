package xyz.qweru.geo.core.helper.tree

import xyz.qweru.geo.core.config.ConfigType

data class SystemContext(val systemFilter: SystemFilter? = null, val settingFilter: SettingFilter? = null) {
    companion object {
        fun of(type: ConfigType) = SystemContext(type.systemFilter, type.settingFilter)
    }
}
