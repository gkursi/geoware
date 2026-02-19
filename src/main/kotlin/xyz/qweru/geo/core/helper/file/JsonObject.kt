package xyz.qweru.geo.core.helper.file

import com.google.gson.JsonElement
import com.google.gson.JsonObject

operator fun JsonObject.set(name: String, value: Number) =
    addProperty(name, value)

operator fun JsonObject.set(name: String, value: String) =
    addProperty(name, value)

operator fun JsonObject.set(name: String, value: JsonElement) =
    add(name, value)
