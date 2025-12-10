package xyz.qweru.geo.core

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import xyz.qweru.geo.client.helper.player.inventory.InvHelper
import xyz.qweru.geo.core.command.CommandManager
import xyz.qweru.geo.core.event.EventBus
import xyz.qweru.geo.core.game.combat.CombatEventHandler
import xyz.qweru.geo.core.game.combat.CombatState
import xyz.qweru.geo.core.game.combat.TargetTracker
import xyz.qweru.geo.core.game.movement.MovementTicker
import xyz.qweru.geo.core.game.rotation.RotationHandler
import xyz.qweru.geo.core.tracking.bot.BotTracker
import xyz.qweru.geo.extend.kotlin.file.findOrCreateDir

object Core {
    const val MOD = "GeoWare"
    const val PREFIX = "::"

    @JvmField val mc: Minecraft = Minecraft.getInstance()
    @JvmField val logger: Logger = LoggerFactory.getLogger(MOD)
    @JvmField val dir = FabricLoader.getInstance().gameDir.findOrCreateDir(MOD)

    fun init() {
        manage(CommandManager)
        manage(MovementTicker)
        manage(InvHelper)
        manage(CombatState.SELF)
        manage(CombatState.TARGET)
        manage(CombatEventHandler)
        manage(TargetTracker)
        manage(RotationHandler)
        manage(BotTracker)
    }

    private fun manage(o: Any) {
        EventBus.subscribe(o)
    }

}