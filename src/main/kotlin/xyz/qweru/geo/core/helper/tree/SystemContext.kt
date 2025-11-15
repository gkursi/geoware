package xyz.qweru.geo.core.helper.tree

import xyz.qweru.geo.core.system.config.ConfigType
import java.util.Optional

data class SystemContext(val systemFilter: SystemFilter? = null, val settingFilter: SettingFilter? = null) {
    companion object {
        fun of(type: ConfigType) = SystemContext(type.systemFilter, type.settingFilter)
    }
}
