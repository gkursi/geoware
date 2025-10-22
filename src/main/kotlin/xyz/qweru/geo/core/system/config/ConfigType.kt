package xyz.qweru.geo.core.system.config

import com.google.gson.JsonObject
import xyz.qweru.geo.core.system.System
import xyz.qweru.geo.core.system.friend.Friends
import xyz.qweru.geo.core.system.helper.tree.SettingFilter
import xyz.qweru.geo.core.system.helper.tree.SystemFilter
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.core.system.module.Modules
import xyz.qweru.geo.core.system.setting.SettingUsage
import xyz.qweru.geo.core.system.setting.Settings

// slightly overengineered config system
enum class ConfigType(val id: String, val systemFilter: SystemFilter, val settingFilter: SettingFilter) {
    ALL("all", { system, _ -> if (system.type == System.Type.ROOT) system is Modules || system is Friends else true }, { _, _ -> true }),
    MODULE("module", { _, module -> module is Module || module is Modules || module is Settings }, { _, _ -> true }),
    FUNCTIONAL("functional", { _, module -> module is Module || module is Modules || module is Settings }, { _, setting -> !setting.hasUsage(SettingUsage.VISUAL) }),
    VISUAL("visual", { _, module -> module is Module || module is Modules || module is Settings }, { _, setting -> setting.hasUsage(SettingUsage.VISUAL) }),
    COLOR("color", { _, module -> module is Module || module is Modules || module is Settings }, { _, setting -> setting.hasUsage(SettingUsage.COLOR) }),
    FRIEND("friend", { parent, system -> system.type == System.Type.ROOT && system is Friends }, { _, _ -> false});

    companion object {
        fun fromJson(json: JsonObject): ConfigType? = fromId(json.get("id")?.asString ?: "")
        fun fromId(id: String): ConfigType? {
            if (id == "") return null
            for (type in entries) {
                if (type.id == id) return type
            }
            return null
        }
    }

    fun addTo(json: JsonObject) {
        json.addProperty("id", id)
    }

    val hasModules: Boolean
        get() = this == ALL || this == MODULE
    val hasVisuals: Boolean
        get() = this == ALL || this == VISUAL
    val hasColors: Boolean
        get() = this == ALL || this == VISUAL || this == COLOR
    val hasFriends: Boolean
        get() = this == ALL || this == FRIEND
}