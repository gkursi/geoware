package xyz.qweru.geo.abstraction.game

import net.minecraft.client.KeyMapping
import net.minecraft.client.Options
import org.lwjgl.glfw.GLFW
import xyz.qweru.geo.core.Core
import xyz.qweru.geo.mixin.input.KeyBindingAccessor
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

object GameOptions {
    private val options: Options
        get() = Core.mc.options

    /* Binds */
    var forwardKey by wrapBind(options.keyUp)
    var backKey by wrapBind(options.keyDown)
    var leftKey by wrapBind(options.keyLeft)
    var rightKey by wrapBind(options.keyRight)
    var jumpKey by wrapBind(options.keyJump)
    var sneakKey by wrapBind(options.keyShift)
    var sprintKey by wrapBind(options.keySprint)
    var useKey by wrapBind(options.keyUse)
    var attackKey by wrapBind(options.keyAttack)

    /* Misc */
    val moving: Boolean
        get() = forwardKey || backKey || leftKey || rightKey

    /**
     * Used to avoid ide warning
     */
    fun syncBind(field: KMutableProperty0<Boolean>) =
        field.set(field.get())

    fun syncMovement() {
        syncBind(this::forwardKey)
        syncBind(this::backKey)
        syncBind(this::leftKey)
        syncBind(this::rightKey)
    }

    private fun wrapBind(bind: KeyMapping): KeyBindingWrapper = KeyBindingWrapper(bind)

    private class KeyBindingWrapper(val bind: KeyMapping) {
        operator fun getValue(u: Any?, property: KProperty<*>) =
            // differentiate mouse and keyboard bindings
            !bind.isUnbound && Core.mc.screen == null && (bind as KeyBindingAccessor).geo_getBound().value.let {
                if (it <= GLFW.GLFW_MOUSE_BUTTON_LAST) return@let GLFW.glfwGetMouseButton(Window.handle, it) == GLFW.GLFW_PRESS
                else return@let GLFW.glfwGetKey(Window.handle, it) == GLFW.GLFW_PRESS
            }

        operator fun setValue(u: Any?, property: KProperty<*>, v: Boolean) {
            bind.isDown = v
        }
    }
}