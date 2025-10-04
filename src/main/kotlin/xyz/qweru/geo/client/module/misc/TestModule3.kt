package xyz.qweru.geo.client.module.misc

import xyz.qweru.geo.core.module.Module

class TestModule3() : Module("TestModule3") {
    fun doThing() {

    }

    override fun enable() {
        enabled = false
    }
}