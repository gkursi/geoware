package xyz.qweru.geo.core.config

import com.google.gson.JsonObject
import org.apache.commons.lang3.text.WordUtils
import xyz.qweru.geo.core.system.System
import xyz.qweru.geo.core.system.impl.friend.Friends
import xyz.qweru.geo.core.helper.tree.SettingFilter
import xyz.qweru.geo.core.helper.tree.SystemFilter
import xyz.qweru.geo.core.system.impl.module.Module
import xyz.qweru.geo.core.system.impl.module.Modules
import xyz.qweru.geo.core.system.impl.setting.SettingUsage
import xyz.qweru.geo.core.system.impl.setting.Settings

enum class ConfigType(val systemFilter: SystemFilter, val settingFilter: SettingFilter) {
    ALL(
        { _, _ -> true },
        { _, _ -> true }
    ),
    ALL_MODULE(
        { _, module -> module.isModule },
        { _, _ -> true }
    ),
    MODULE(
        { _, module -> module.isModule },
        { _, setting -> !setting.hasUsage(SettingUsage.VISUAL) }
    ),
    VISUAL(
        { _, module -> module.isModule },
        { _, setting -> setting.hasUsage(SettingUsage.VISUAL) }
    ),
    COLOR(
        { _, module -> module.isModule },
        { _, setting -> setting.hasUsage(SettingUsage.COLOR) }
    ),
    FRIEND(
        { _, system -> system is Friends },
        { _, _ -> false}
    );

    val id: String = this.name
        .lowercase()
        .replace("_", " ")
        .also(WordUtils::capitalize)
        .replace(" ", "")

    companion object {
        fun fromJson(json: JsonObject): ConfigType? =
            fromId(json["id"]?.asString ?: "")

        fun fromId(id: String): ConfigType? {
            if (id == "") return null
            for (type in entries) {
                if (type.id == id) return type
            }
            return null
        }
    }

    fun addToJson(json: JsonObject) {
        json.addProperty("id", id)
    }

    val containsModules: Boolean
        get() = this == ALL || this == ALL_MODULE
    val containsFriends: Boolean
        get() = this == ALL || this == FRIEND
}

val System.isModule: Boolean
    get() = this is Module || this is Modules || this is Settings