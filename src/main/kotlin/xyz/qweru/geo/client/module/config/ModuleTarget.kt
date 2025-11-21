package xyz.qweru.geo.client.module.config

import xyz.qweru.geo.core.tracking.bot.BotType
import xyz.qweru.geo.core.system.module.Module

class ModuleTarget : Module("Target", "Global target settings") {
    val sc = settings.group("Conditions")
    val abandonRange by sc.float("Abandon Range", "Max range the target can be before abandoning it", 8f, 1f, 20f)

    val se = settings.group("Exclude")
    val excludeFriends by se.boolean("Friends", "Won't target friends", true)
    val botModes by se.multiEnum("Bots", "Bot modes to exclude", BotType.SIMPLE)
    val matrixTag by se.boolean("Matrix Tag", "Finds matrix bots with odd tab list entries. Only use on servers with ranks.", false)
}