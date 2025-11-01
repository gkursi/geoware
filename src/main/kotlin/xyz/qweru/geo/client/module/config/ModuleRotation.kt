package xyz.qweru.geo.client.module.config

import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module

class ModuleRotation : Module("Rotation", "How to rotate", Category.CONFIG) {
    private val sg = settings.group("General")
    private val sf = settings.group("Fix")

    val speed by sg.float("Speed", "Rotation speed", 1f, 0.1f, 2f)
    val linear by sg.boolean("Linear", "Linear rotations", false)
    val moveFix by sf.boolean("Fix Move", "Fix Movement", true)
    val mouseFix by sf.boolean("Fix Mouse", "Fix crosshair target", true)
    val gcdFix by sf.boolean("Fix Sens", "Fix gcd", true)
}