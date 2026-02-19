package xyz.qweru.geo.client.module.config

import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module

class ModulePacket : Module("Packets", "How the client handles packets", Category.CONFIG) {
    private val sg = settings.general

    val delay by sg.enum("Delay", "Which delay mode to use", Delay.MAX)
    val time by sg.longRange("Time", "Length of delay", 1000L..1250L, 0L..5000)

    enum class Delay {
        LIMITLESS, /* AUTO,*/ MAX
    }
}