package xyz.qweru.geo.client.helper.input

import net.minecraft.client.option.GameOptions
import net.minecraft.client.option.KeyBinding
import org.lwjgl.glfw.GLFW
import xyz.qweru.geo.core.Global.mc
import xyz.qweru.geo.mixin.input.KeyBindingAccessor
import kotlin.reflect.KProperty

/**
 * Wrapper for GameOptions
 */
object GameInput {
    private val options: GameOptions
        get() = mc.options

    var forwardKey by wrap(options.forwardKey)
    var backKey by wrap(options.backKey)
    var leftKey by wrap(options.leftKey)
    var rightKey by wrap(options.rightKey)

    var jumpKey by wrap(options.jumpKey)
    var sneakKey by wrap(options.sneakKey)

    val moving: Boolean
        get() = forwardKey || backKey || leftKey || rightKey

    var useKey by wrap(options.useKey)
    var attackKey by wrap(options.attackKey)

    private fun wrap(bind: KeyBinding): KeyBindingWrapper = KeyBindingWrapper(bind)

    private class KeyBindingWrapper(val bind: KeyBinding) {
        operator fun getValue(u: Any?, property: KProperty<*>) =
            !bind.isUnbound && (bind as KeyBindingAccessor).geo_getBound().code.let {
                if (it <= GLFW.GLFW_MOUSE_BUTTON_LAST)
                    return@let GLFW.glfwGetMouseButton(mc.window.handle, (bind as KeyBindingAccessor).geo_getBound().code) == GLFW.GLFW_PRESS
                else
                    return@let GLFW.glfwGetKey(mc.window.handle, (bind as KeyBindingAccessor).geo_getBound().code) == GLFW.GLFW_PRESS
            }
        operator fun setValue(u: Any?, property: KProperty<*>, v: Boolean) {
            bind.isPressed = v
        }
    }
}