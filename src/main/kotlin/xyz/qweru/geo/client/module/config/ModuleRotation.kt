package xyz.qweru.geo.client.module.config

import xyz.qweru.geo.core.Core.mc
import xyz.qweru.geo.core.game.rotation.InterpolationEngine
import xyz.qweru.geo.core.game.rotation.RotationHandler
import xyz.qweru.geo.core.game.rotation.interpolate.ConstantInterpolationEngine
import xyz.qweru.geo.core.game.rotation.interpolate.HumanInterpolationEngine
import xyz.qweru.geo.core.game.rotation.interpolate.InstantInterpolationEngine
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.kotlin.math.wrapped
import xyz.qweru.geo.extend.minecraft.game.thePlayer

class ModuleRotation : Module("Rotation", "How to rotate", Category.CONFIG) {
    private val sg = settings.general
    private val sa = settings.group("Point")
    private val sf = settings.group("Fix")
    private val sh = settings.group("Humanized")
    private val sc = settings.group("Constant")

    val speed by sg.float("Speed", "Rotation speed", 75f, 0.1f, 200f)
    val diff by sg.float("Allowed Diff", "Allowed difference from a rotation", 5f, 1f, 90f)
    @Suppress("UNUSED")
    val randomizer by sg.enum("Randomizer", "Randomizer to use", Engine.HUMANIZED)
        .onChange { RotationHandler.engine = it.value.engine }

    val source by sa.enum("Calculation Source", "Which rotation to use for aim point calculations", Source.SERVER)

    val moveFix by sf.boolean("Fix Move", "Fix Movement", true)
    val mouseFix by sf.boolean("Fix Mouse", "Fix crosshair target", true)
    val gcdFix by sf.boolean("Fix Sens", "Fix gcd", true)

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
    val mousePadPenaltyMax by sh.float("Max Penalty", "Maximum penalty to apply", 0f, 0f, 0.95f)
        .visible { mousePad }
    val speedUp by sh.boolean("Accelerate", "Don't immediately go full speed", true)
    val speedYaw by sh.float("Max Speed", "Will reach max speed at this rotation %", .25f, 0f, 1f)

    val step by sc.float("Step", "Rotation step each tick", 30f, 1f, 90f)
    val offset by sc.float("Offset", "Max random offset", 0.2f, 0f, 1f)

    @Suppress("UNUSED")
    enum class Engine(val engine: InterpolationEngine) {
        CONSTANT(ConstantInterpolationEngine),
        INSTANT(InstantInterpolationEngine),
        HUMANIZED(HumanInterpolationEngine)
    }

    enum class Source {
        CLIENT {
            override val yaw: Float
                get() = mc.thePlayer.yRot.wrapped
            override val pitch: Float
                get() = mc.thePlayer.xRot
        },
        SERVER {
            override val yaw: Float
                get() = RotationHandler.rot[0].wrapped
            override val pitch: Float
                get() = RotationHandler.rot[1]
        };

        abstract val yaw: Float
        abstract val pitch: Float
    }
}