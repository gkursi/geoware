package xyz.qweru.geo.core.manager

import xyz.qweru.geo.core.event.Events
import xyz.qweru.geo.core.manager.ticking.MovementTicker
import xyz.qweru.geo.helper.player.InvHelper

/**
 * Misc managing classes that don't implement the system class
 */
object Managers {

    fun init() {
        manage(MovementTicker)
        manage(InvHelper)
    }

    private fun manage(o: Any) {
        Events.subscribe(o)
    }

}