package xyz.qweru.geo.client.module.visual

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.HumanoidArm
import org.joml.Vector3f
import xyz.qweru.geo.core.system.impl.module.Category
import xyz.qweru.geo.core.system.impl.module.Module
import xyz.qweru.geo.core.system.impl.setting.SettingUsage
import xyz.qweru.geo.core.system.impl.setting.Usage

class ModuleViewModel : Module("ViewModel", "Change your viewmodel", Category.VISUAL) {
    val sg = settings.general
    val mode by sg.enum("Mode", "Swing mode", Mode.COMPATIBILITY)

    @Usage(SettingUsage.VISUAL, SettingUsage.POSITION)
    val mainHand by settings.group("Main Hand")
    var mainX by mainHand.float("Main X", "Hand transformation", 0f, -5f, 5f)
    var mainY by mainHand.float("Main Y", "Hand transformation", 0f, -5f, 5f)
    var mainZ by mainHand.float("Main Z", "Hand transformation", 0f, -5f, 5f)
    var mainSX by mainHand.float("Main Scale X", "Hand transformation", 1f, -5f, 5f)
    var mainSY by mainHand.float("Main Scale Y", "Hand transformation", 1f, -5f, 5f)
    var mainSZ by mainHand.float("Main Scale Z", "Hand transformation", 1f, -5f, 5f)
    var mainRX by mainHand.float("Main Rot X", "Hand transformation", 0f, -5f, 5f)
    var mainRY by mainHand.float("Main Rot Y", "Hand transformation", 0f, -5f, 5f)
    var mainRZ by mainHand.float("Main Rot Z", "Hand transformation", 0f, -5f, 5f)

    @Usage(SettingUsage.VISUAL, SettingUsage.POSITION)
    val offHand by settings.group("Off Hand")
    var offX by offHand.float("Off X", "Hand transformation", 0f, -5f, 5f)
    var offY by offHand.float("Off Y", "Hand transformation", 0f, -5f, 5f)
    var offZ by offHand.float("Off Z", "Hand transformation", 0f, -5f, 5f)
    var offSX by offHand.float("Off Scale X", "Hand transformation", 1f, -5f, 5f)
    var offSY by offHand.float("Off Scale Y", "Hand transformation", 1f, -5f, 5f)
    var offSZ by offHand.float("Off Scale Z", "Hand transformation", 1f, -5f, 5f)
    var offRX by mainHand.float("Off Rot X", "Hand transformation", 0f, -5f, 5f)
    var offRY by mainHand.float("Off Rot Y", "Hand transformation", 0f, -5f, 5f)
    var offRZ by mainHand.float("Off Rot Z", "Hand transformation", 0f, -5f, 5f)

    @Usage(SettingUsage.VISUAL)
    val animations by settings.group("Animations")
    var equipOffset by animations.boolean("Equip Anim", "Equip animation", true)
    var handSway by animations.boolean("Hand Sway", "Hand sway", true)
    var handInterpolation by animations.boolean("Hand Interp", "Delayed hand rotation", true)

    @Usage(SettingUsage.VISUAL, SettingUsage.POSITION)
    val swing by settings.group("Swing").visible { mode == Mode.COMPATIBILITY }
    var swingSpeed by swing.float("Speed", "Swing speed", 1f, 0.01f, 3f)
    var swingX by swing.float("Swing X", "Swing position", -0.4f, -2f, 2f)
    var swingY by swing.float("Swing Y", "Swing position", 0.2f, -2f, 2f)
    var swingZ by swing.float("Swing Z", "Swing position", -0.2f, -2f, 2f)
    var swingRPX by swing.float("Swing RPX", "Swing progress-based rotation degrees", -120f, -180f, 180f)
    var swingRPY by swing.float("Swing RPY", "Swing progress-based rotation degrees", 0f, -180f, 180f)
    var swingRPYOff by swing.float("Swing RPY Off", "Swing progress-based rotation degree offset", 0f, -180f, 180f)
    var swingRAY by swing.float("Swing RAY", "Swing absolute rotation degrees", 0f, -180f, 180f)
    var swingRPZ by swing.float("Swing RPZ", "Swing progress-based rotation degrees", 0f, -180f, 180f)

    @Usage(SettingUsage.VISUAL, SettingUsage.POSITION)
    val eat by settings.group("Eat")
    var eatJitter by eat.float("Jitter", "Eating Y jitter", 0.1f, 0f, 2f)
    var eatX by eat.float("Eat X", "Eat position", 0.6f, -2f, 2f)
    var eatY by eat.float("Eat Y", "Eat position", -0.5f, -2f, 2f)
    var eatZ by eat.float("Eat Z", "Eat position", 0.0f, -2f, 2f)
    var eatRX by eat.float("Eat RX", "Eat degrees", 10f, -180f, 180f)
    var eatRY by eat.float("Eat RY", "Eat degrees", 90f, -180f, 180f)
    var eatRZ by eat.float("Eat RZ", "Eat degrees", 30f, -180f, 180f)

    fun getScale(hand: InteractionHand): Vector3f =
        if (hand == InteractionHand.MAIN_HAND) vec(mainSX, mainSY, mainSZ)
        else vec(offSX, offSY, offSZ)

    fun getOffset(hand: InteractionHand): Vector3f =
        if (hand == InteractionHand.MAIN_HAND) vec(mainX, mainY, mainZ)
        else vec(offX, offY, offZ)

    fun getRot(hand: InteractionHand): Vector3f =
        if (hand == InteractionHand.MAIN_HAND) vec(mainRX, mainRY, mainRZ)
        else vec(offRX, offRY, offRZ)

    /**
     * @param side 1/-1 depending on which arm is being rendered
     */
    fun swing(swingProgress: Float, stack: PoseStack, side: Int, arm: HumanoidArm) {
        // todo
    }

    private fun vec(x: Float, y: Float, z: Float): Vector3f = Vector3f(x, y, z)

    enum class Mode {
        NORMAL,
        COMPATIBILITY
    }

}