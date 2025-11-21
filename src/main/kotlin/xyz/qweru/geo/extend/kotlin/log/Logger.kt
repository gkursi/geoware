package xyz.qweru.geo.extend.kotlin.log

import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.impl.launch.FabricLauncherBase
import org.slf4j.Logger

private val env = FabricLoader.getInstance().isDevelopmentEnvironment

// idk how to log at the debug level
fun Logger.dbg(msg: String) {
    if (env) this.info("[dbg] $msg")
}