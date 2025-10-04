package xyz.qweru.geo.core

import net.minecraft.client.MinecraftClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Glob {
    const val mod = "GeoWare"
    @JvmField
    val mc: MinecraftClient = MinecraftClient.getInstance()
    @JvmField
    val logger: Logger = LoggerFactory.getLogger(mod)

    // temp global config
    const val prefix = "::"
}