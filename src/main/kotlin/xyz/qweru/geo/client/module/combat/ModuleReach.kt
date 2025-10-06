package xyz.qweru.geo.client.module.combat

import xyz.qweru.geo.core.module.Category
import xyz.qweru.geo.core.module.Module

class ModuleReach : Module("Reach", "Lets you reach further", Category.COMBAT) {
    val sGeneral = settings.group("General")
    var entity by sGeneral.float("Entity", "Entity reach", 3.2f, 3f, 6f)
    var block by sGeneral.float("Block", "Blcok reach", 3.2f, 3f, 6f)
}