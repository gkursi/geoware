package xyz.qweru.geo.core.game.interaction

import xyz.qweru.geo.core.helper.manage.Proposal

interface Interaction : Proposal {
    fun interact()
}