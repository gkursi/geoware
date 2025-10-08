package xyz.qweru.geo.core.module

data class Category(val name: String, val icon: String = "") {
    companion object {
        val COMBAT = Category("Combat")
        val PLAYER = Category("Player")
        val MOVEMENT = Category("Movement")
        val VISUAL = Category("Visual")
        val MISC = Category("Miscellaneous")
        val CONFIG = Category("Config")
    }
}