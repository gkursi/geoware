package xyz.qweru.geo.core.manager

import xyz.qweru.geo.core.event.EventBus
import xyz.qweru.geo.core.manager.combat.CombatEventHandler
import xyz.qweru.geo.core.manager.combat.CombatState
import xyz.qweru.geo.core.manager.combat.TargetTracker
import xyz.qweru.geo.core.manager.command.CommandManager
import xyz.qweru.geo.core.manager.movement.MovementTicker
import xyz.qweru.geo.client.helper.player.inventory.InvHelper
import xyz.qweru.geo.core.manager.rotation.RotationHandler
import xyz.qweru.geo.core.render.skija.SkijaManager

/**
 * Misc managing classes that don't implement the system class
 */
object Managers {

    fun init() {
        manage(CommandManager)
        manage(MovementTicker)
        manage(InvHelper)
        manage(CombatState.SELF)
        manage(CombatState.TARGET)
        manage(CombatEventHandler)
        manage(TargetTracker)
        manage(SkijaManager)
        manage(RotationHandler)
    }

    private fun manage(o: Any) {
        EventBus.subscribe(o)
    }

}