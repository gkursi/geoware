package xyz.qweru.geo.extend.kotlin.string

import com.mojang.brigadier.StringReader

fun StringReader.readAllRemaining(): String {
    val text: String = remaining
    cursor = totalLength
    return text
}