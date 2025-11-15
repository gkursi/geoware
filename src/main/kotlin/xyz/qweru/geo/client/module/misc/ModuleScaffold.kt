package xyz.qweru.geo.client.module.misc

import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module

class ModuleScaffold : Module("Scaffold", "Automatically places blocks below you", Category.MISC) {
    private val sg = settings.group("General")

    private val aim = sg.enum("Aim", "Aim mode", Action.AUTO)
    private val place = sg.enum("Place", "Aim mode", Action.AUTO)

    private enum class Action {
        AUTO,
        MANUAL
    }
}