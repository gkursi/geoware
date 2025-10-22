package xyz.qweru.geo.core

import net.fabricmc.loader.impl.FabricLoaderImpl
import net.minecraft.client.MinecraftClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import xyz.qweru.geo.extend.findOrCreateDir

object Global {
    const val MOD = "GeoWare"
    @JvmField val DIRECTORY = FabricLoaderImpl.INSTANCE.gameDir.findOrCreateDir(MOD)
    // temp global config
    const val PREFIX = "::"

    @JvmField val mc: MinecraftClient = MinecraftClient.getInstance()
    @JvmField val logger: Logger = LoggerFactory.getLogger(MOD)
}