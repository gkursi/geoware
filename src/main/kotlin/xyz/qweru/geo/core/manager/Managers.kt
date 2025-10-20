package xyz.qweru.geo.core.manager

import xyz.qweru.geo.core.event.Events
import xyz.qweru.geo.core.manager.combat.CombatEventHandler
import xyz.qweru.geo.core.manager.combat.CombatState
import xyz.qweru.geo.core.manager.combat.TargetTracker
import xyz.qweru.geo.core.manager.movement.MovementTicker
import xyz.qweru.geo.helper.player.InvHelper

/**
 * Misc managing classes that don't implement the system class
 */
object Managers {

    fun init() {
        manage(MovementTicker)
        manage(InvHelper)
        manage(CombatState.SELF)
        manage(CombatState.TARGET)
        manage(CombatEventHandler)
        manage(TargetTracker)
    }

    private fun manage(o: Any) {
        Events.subscribe(o)
    }

}