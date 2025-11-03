package xyz.qweru.geo.client.module.config

import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module

class ModuleRotation : Module("Rotation", "How to rotate", Category.CONFIG) {
    private val sg = settings.group("General")
    private val sf = settings.group("Fix")
    private val sh = settings.group("Human")

    val speed by sg.float("Speed", "Rotation speed", 75f, 0.1f, 200f)
    val nonlinear by sg.boolean("Nonlinear", "Calculate from the current yaw instead of the base yaw", false)
    val diff by sg.float("Allowed Diff", "Allowed difference from a rotation", 5f, 1f, 90f)

    val moveFix by sf.boolean("Fix Move", "Fix Movement", true)
    val mouseFix by sf.boolean("Fix Mouse", "Fix crosshair target", true)
    val gcdFix by sf.boolean("Fix Sens", "Fix gcd", true)

    val humanize by sh.boolean("Humanize", "More human-like rotations", true)
    val flick by sh.boolean("Flick", "Allow flicking", true)
    val flickRange by sh.floatRange("Flick Range", "Threshold for flicking", 40f..100f, 0f..180f)
        .visible { flick }
    val flickBoost by sg.float("Flick Boost", "Flicking speed", 2f, 0.1f, 3f)
        .visible { flick }
    val micro by sh.boolean("Micro", "Allow instant micro adjustments", true)
    val microRange by sh.floatRange("Micro Range", "Threshold for flicking", 0f..16f, 0f..180f)
        .visible { micro }
    val mousePad by sh.boolean("Mouse Pad", "Slightly slow down rotations when rotating too far to simulate limited mouse space", false)
    val mousePadSize by sh.float("Pad Size", "How much can we rotate to each direction before slowing down", 160f, 90f, 720f)
        .visible { mousePad }
    val mousePadPenalty by sh.float("Tick Penalty", "How much penalty to apply each tick", 0.05f, 0.001f, 0.5f)
        .visible { mousePad }
    val mousePadPenaltyMax by sh.float("Max Penalty", "Maximum penalty to apply", 0.25f, 0f, 0.95f)
        .visible { mousePad }
}