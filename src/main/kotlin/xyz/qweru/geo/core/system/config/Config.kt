package xyz.qweru.geo.core.system.config

import com.google.gson.JsonObject

data class Config(val name: String, var type: ConfigType, var json: JsonObject) {
    companion object {
        val EMPTY = Config("", ConfigType.ALL, JsonObject())
    }

    val isEmpty: Boolean
        get() = this == EMPTY
}