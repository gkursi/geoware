package xyz.qweru.geo.core.system.helper.tree

import xyz.qweru.geo.core.system.config.ConfigType
import java.util.Optional

data class SystemContext(val systemFilter: Optional<SystemFilter> = Optional.empty(), val settingFilter: Optional<SettingFilter> = Optional.empty()) {
    companion object {
        fun of(type: ConfigType) = SystemContext(Optional.of(type.systemFilter), Optional.of(type.settingFilter))
    }
}
