package xyz.qweru.geo.client.module.config

import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module

class ModuleSwap : Module("Swap", "Global swap settings", Category.CONFIG) {
    val sg = settings.group("General")
    val scrollSwap by sg.boolean("Scroll Swap", "Imitate scrolling for slots further away", false)
    val scrollSwapMin by sg.int("Min Scroll", "Won't scroll when swapping to a slot before this one", 5, 1, 9)
    val silentSwap by sg.boolean("Silent Swap", "Silent swap", false)
//    val swapDelay by sg.delay("Delay", "Delay between swaps", 30, 50, 0, 500)
}