package xyz.qweru.geo.core.config

import com.google.gson.JsonObject

data class Config(val name: String, var type: ConfigType, var json: JsonObject) {
    companion object {
        val EMPTY = Config("", ConfigType.ALL, JsonObject())
    }

    val isEmpty: Boolean
        get() = this == EMPTY
}