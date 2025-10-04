package xyz.qweru.geo.client.module.combat

import xyz.qweru.geo.core.module.Category
import xyz.qweru.geo.core.module.Module

class ModuleHitbox : Module("Hitbox", "Expand player hitboxes", Category.COMBAT) {
    val sGeneral = settings.group("General")
    var size by sGeneral.float("Size", "Size of the hitbox", 1.15f, 1f, 3f)
}