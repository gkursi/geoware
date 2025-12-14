package xyz.qweru.geo.client.module.misc

import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module

class ModuleScaffold : Module("Scaffold", "Automatically places blocks below you", Category.MISC) {
    private val sg = settings.group("General")

    @Handler
    fun tick(e: PreTickEvent) {

    }

    enum class Mode {
        GRIM
    }
}