package xyz.qweru.geo.client.module.move

import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.impl.module.Category
import xyz.qweru.geo.core.system.impl.module.Module
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import xyz.qweru.geo.imixin.IPlayer

class ModuleNoSlow : Module("NoSlow", "Remove different slowdowns", Category.MOVEMENT) {

    val si = settings.group("Items")
    val sm = settings.group("Misc")

    val items by si.boolean("Items", "Change item slowdown", true)
    val itemSpeed by si.float("Item Slowdown", "How much should items slow you", 0f, 0f, 1f)

    val jumpDelay by sm.boolean("No Jump Delay", "No jump delay", true)
    val blockPush by sm.boolean("No Block Push", "No block push", true)

    @Handler
    private fun preTick(e: PreTickEvent) {
        if (!inGame || !jumpDelay) return
        (mc.thePlayer as IPlayer).geo_setJumpDelay(1)
    }

}