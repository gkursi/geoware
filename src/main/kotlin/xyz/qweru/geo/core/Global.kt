package xyz.qweru.geo.core

import net.fabricmc.loader.impl.FabricLoaderImpl
import net.minecraft.client.Minecraft
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import xyz.qweru.geo.extend.kotlin.file.findOrCreateDir

object Global {
    const val MOD = "GeoWare"
    @JvmField val DIRECTORY = FabricLoaderImpl.INSTANCE.gameDir.findOrCreateDir(MOD)
    // temp global config
    const val PREFIX = "::"

    @JvmField val mc: Minecraft = Minecraft.getInstance()
    @JvmField val logger: Logger = LoggerFactory.getLogger(MOD)
}