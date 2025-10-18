package xyz.qweru.geo.extend

import com.mojang.brigadier.StringReader

fun StringReader.readAllRemaining(): String {
    val text: String = remaining
    cursor = totalLength
    return text
}