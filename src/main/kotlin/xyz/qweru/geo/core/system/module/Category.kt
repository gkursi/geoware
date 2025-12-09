package xyz.qweru.geo.core.system.module

data class Category(val name: String, val icon: String = "") {
    companion object {
        val COMBAT = Category("Combat")
        val PLAYER = Category("Player")
        val MOVEMENT = Category("Movement")
        val VISUAL = Category("Visual")
        val MISC = Category("Miscellaneous")
        val SPECIFIC = Category("Specific")
        val CONFIG = Category("Config")
    }
}