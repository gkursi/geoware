package xyz.qweru.geo.client.module.visual

import net.minecraft.util.Hand
import org.joml.Vector3f
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module

class ModuleViewModel : Module("ViewModel", "Change your viewmodel", Category.VISUAL) {
    val mainHand = settings.group("Main Hand")
    var mainX by mainHand.float("Main X", "Hand transformation", 0f, -5f, 5f)
    var mainY by mainHand.float("Main Y", "Hand transformation", 0.2f, -5f, 5f)
    var mainZ by mainHand.float("Main Z", "Hand transformation", 0f, -5f, 5f)
    var mainSX by mainHand.float("Main Scale X", "Hand transformation", 0.4f, -5f, 5f)
    var mainSY by mainHand.float("Main Scale Y", "Hand transformation", 0.7f, -5f, 5f)
    var mainSZ by mainHand.float("Main Scale Z", "Hand transformation", 0.4f, -5f, 5f)

    val offHand = settings.group("Off Hand")
    var offX by offHand.float("Off X", "Hand transformation", 0f, -5f, 5f)
    var offY by offHand.float("Off Y", "Hand transformation", -0.05f, -5f, 5f)
    var offZ by offHand.float("Off Z", "Hand transformation", -0.5f, -5f, 5f)
    var offSX by offHand.float("Off Scale X", "Hand transformation", 0.3f, -5f, 5f)
    var offSY by offHand.float("Off Scale Y", "Hand transformation", 0.2f, -5f, 5f)
    var offSZ by offHand.float("Off Scale Z", "Hand transformation", 0.3f, -5f, 5f)

    val animations = settings.group("Animations")
    var equipOffset by animations.boolean("Equip Anim", "Equip animation", false)
    var handSway by animations.boolean("Hand Sway", "Hand sway", false)
    var handInterp by animations.boolean("Hand Interp", "Delayed hand rotation", false)

    val swing = settings.group("Swing")
    var swingSpeed by swing.float("Speed", "Swing speed", 0.5f, 0.01f, 3f)
    var swingX by swing.float("Swing X", "Swing position", 0f, -2f, 2f)
    var swingY by swing.float("Swing Y", "Swing position", 0.1f, -2f, 2f)
    var swingZ by swing.float("Swing Z", "Swing position", 0f, -2f, 2f)
    var swingRPX by swing.float("Swing RPX", "Swing progress-based rotation degrees", -120f, -180f, 180f)
    var swingRPY by swing.float("Swing RPY", "Swing progress-based rotation degrees", 0f, -180f, 180f)
    var swingRPYOff by swing.float("Swing RPY Off", "Swing progress-based rotation degree offset", 0f, -180f, 180f)
    var swingRAY by swing.float("Swing RAY", "Swing absolute rotation degrees", 0f, -180f, 180f)
    var swingRPZ by swing.float("Swing RPZ", "Swing progress-based rotation degrees", 0f, -180f, 180f)

    val eat = settings.group("Eat")
    var eatJitter by eat.float("Jitter", "Eating Y jitter", 0.3f, 0f, 2f)
    var eatX by eat.float("Eat X", "Eat position", 0.6f, -2f, 2f)
    var eatY by eat.float("Eat Y", "Eat position", -0.5f, -2f, 2f)
    var eatZ by eat.float("Eat Z", "Eat position", 0.0f, -2f, 2f)
    var eatRX by eat.float("Eat RX", "Eat degrees", 10f, -180f, 180f)
    var eatRY by eat.float("Eat RY", "Eat degrees", 90f, -180f, 180f)
    var eatRZ by eat.float("Eat RZ", "Eat degrees", 30f, -180f, 180f)

    fun getScale(hand: Hand): Vector3f =
        if (hand == Hand.MAIN_HAND) vec(mainSX, mainSY, mainSZ)
        else vec(offSX, offSY, offSZ)

    fun getOffset(hand: Hand): Vector3f =
        if (hand == Hand.MAIN_HAND) vec(mainX, mainY, mainZ)
        else vec(offX, offY, offZ)

    private fun vec(x: Float, y: Float, z: Float): Vector3f = Vector3f(x, y, z)

}