package xyz.qweru.geo.client.module.misc

import xyz.qweru.geo.client.setting.BooleanSetting
import xyz.qweru.geo.core.module.Module

class TestModule2() : Module("TestModule2") {
    val sgGeneral = settings.group("General")
    val sgMisc = settings.group("Misc")

    fun doThing() {

    }

    override fun enable() {
        enabled = false
    }
}