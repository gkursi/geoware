package xyz.qweru.geo

import net.fabricmc.api.ModInitializer
import xyz.qweru.geo.core.Core
import xyz.qweru.geo.core.config.Config
import xyz.qweru.geo.core.config.Configs
import xyz.qweru.geo.core.event.EventBus
import xyz.qweru.geo.core.event.PostInitEvent
import xyz.qweru.geo.core.system.Systems
import xyz.qweru.geo.core.ui.UI

class GeoWare : ModInitializer {
    override fun onInitialize() {
        val i = System.nanoTime()

        Core.init()
        Systems.init()
        UI.init()
        Configs.init()

        Core.logger.info("Initialized ${Core.MOD} in ${(System.nanoTime() - i)/1000000}ms")

        EventBus.post(PostInitEvent)
    }
}
