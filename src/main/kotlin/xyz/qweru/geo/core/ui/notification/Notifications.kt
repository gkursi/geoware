package xyz.qweru.geo.core.ui.notification

import xyz.qweru.geo.client.helper.network.ChatHelper
import xyz.qweru.geo.core.Core
import xyz.qweru.geo.core.Core.mc
import xyz.qweru.geo.core.system.module.Module

object Notifications {
    fun onToggle(module: Module) {
        if (mc.player == null) return
        ChatHelper.toggleMessage(module)
    }

    fun info(string: String) {
        Core.logger.info(string)
        if (mc.player == null) return
        ChatHelper.info(string)
    }

    fun warning(string: String) {
        Core.logger.warn(string)
        if (mc.player == null) return
        ChatHelper.warning(string)
    }

    fun error(string: String) {
        Core.logger.error(string)
        if (mc.player == null) return
        ChatHelper.error(string)
    }

}