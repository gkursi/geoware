package xyz.qweru.geo.extend

import java.util.regex.Pattern

private val COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]")

fun String.stripMinecraftColorCodes(): String =
    COLOR_PATTERN.matcher(this).replaceAll("")