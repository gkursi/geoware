package xyz.qweru.geo.client.module.combat

import xyz.qweru.geo.core.system.impl.module.Category
import xyz.qweru.geo.core.system.impl.module.Module

class ModuleReach : Module("Reach", "Lets you reach further", Category.COMBAT) {
    val sGeneral = settings.general
    var entity by sGeneral.float("Entity", "Entity reach", 3f, 3f, 6f)
    var block by sGeneral.float("Block", "Block reach", 3f, 3f, 6f)
}