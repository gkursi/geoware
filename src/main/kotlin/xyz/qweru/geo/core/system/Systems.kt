package xyz.qweru.geo.core.system

import com.google.gson.JsonObject
import xyz.qweru.geo.core.system.config.Configs
import xyz.qweru.geo.core.system.friend.Friends
import xyz.qweru.geo.core.system.module.Modules
import xyz.qweru.geo.core.helper.tree.Walker

/**
 * Parent class for all systems
 */
object Systems : System("systems") {
    override fun init() {
        super.init()
        Walker.walk(this)
        val size = getSubsystems().size
        logger.info("${if(firstInit) "L" else "Rel"}oaded $size system${if (size == 1) "" else "s"} (${Walker.size - size} subsystems)")
    }

    override fun initThis() {
        add(Modules())
        add(Friends())
        add(Configs())
    }

    override fun loadThis(json: JsonObject) = throw AssertionError()
    override fun saveThis(json: JsonObject) = throw AssertionError()
}