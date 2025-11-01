package xyz.qweru.geo.client.module.move

import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module

class ModuleNoSlow : Module("NoSlow", "Remove different slowdowns", Category.MOVEMENT) {

    val si = settings.group("Items")
    val items by si.boolean("Items", "Change item slowdown", true)
    val itemSpeed by si.float("Item Slowdown", "How much should items slow you", 0f, 0f, 1f)

}