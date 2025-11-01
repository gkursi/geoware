package xyz.qweru.geo.client.module.move

import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.Items
import net.minecraft.util.math.MathHelper
import org.lwjgl.glfw.GLFW
import xyz.qweru.geo.client.event.PostMovementTickEvent
import xyz.qweru.geo.client.helper.input.GameInput
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.canGlide
import xyz.qweru.geo.extend.thePlayer
import xyz.qweru.multirender.api.API

class ModuleVulcanElytra : Module("VulcanElytra","Fly without rockets", Category.MOVEMENT) {
    val sg = settings.group("General")
    val mode by sg.enum("Mode", "Mode", Mode.CONTROL)
    val bounce by sg.boolean("Bounce", "Should you bounce off the ground", false)
    val yToKeep by sg.float("Keep Y", "Which y coord to stay above", 320f, -90f, 600f)
        .visible { mode == Mode.KEEP_Y}

    val sb = settings.group("Boost")
    val boostGlide by sb.float("Boost", "How much to boost y elytra movement", 0.25f, -3f, 3f)
    val boostUp by sb.float("Up Boost", "How much to boost y elytra movement when going upwards", 1f, -3f, 3f)
    val boostDown by sb.float("Down Boost", "How much to boost y elytra movement when going downwards", -1f, -3f, 3f)
    val hBoost by sb.float("Horizonal Boost", "Horizontal boost", 1f, 0f, 5f)
    val hMul by sb.float("Horizonal Mul", "Horizontal multiplier", 1f, 0.1f, 5f)
    val hBoostLimit by sb.float("Horizonal Limit", "Horizontal boost limit", 0.4f, 0f, 5f)
    val hControl by sb.boolean("H Control", "Horizontal boost control", false)

    var wasGliding = false
    var gliding = false
    var canGlide = false
    var onGround = false

    @Handler
    private fun onVelocity(event: PostMovementTickEvent) {
        mc.options.jumpKey.isPressed = GameInput.jumpKey
        if (!shouldFly(event)) return

        gliding = mc.thePlayer.isGliding
        canGlide = mc.thePlayer.canGlide
        onGround = mc.thePlayer.isOnGround

        if (canGlide) GameInput.jumpKey = false

        if (shouldBounce()) mc.thePlayer.jump()

        if (gliding && !wasGliding) {
            event.velY += getBoost()
            if (GameInput.moving) {
                var yaw = MathHelper.wrapDegrees(mc.thePlayer.yaw)
                if (hControl) {
                    if (GameInput.backKey) yaw -= 180
                    if (GameInput.leftKey) yaw -= 90
                    if (GameInput.rightKey) yaw += 90
                }
                val hvec = mc.thePlayer.getRotationVector(0f, yaw).multiply(hBoost.toDouble())
                event.velX += hvec.x
                event.velZ += hvec.z
            }
        }


        if (!gliding && canGlide) glide(true)
        else if (shouldStopGlide(event)) glide(false)

        if (gliding) {
            val mul = if (GameInput.moving) hMul else 1f
            event.velX = event.velX * mul
            event.velZ = event.velZ * mul
            event.clampHorizontal(hBoostLimit.toDouble())
        }

        wasGliding = gliding
    }

    private fun shouldStopGlide(e: PostMovementTickEvent): Boolean =
        ((e.velY < 0 && canGlide) || (mode == Mode.HOP)) && gliding && mode != Mode.JUMP

    private fun shouldBounce(): Boolean =
        (bounce && onGround && gliding)
        || (onGround && !gliding && mode == Mode.HOP
            && mc.thePlayer.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA))
    
    private fun shouldFly(e: PostMovementTickEvent): Boolean =
        when (mode) {
            Mode.CONTROL, Mode.HOP -> true
            Mode.KEEP_Y -> mc.thePlayer.y < yToKeep
            Mode.JUMP -> true
        }

    private fun glide(b: Boolean) {
        if (b) {
            // TODO the fuck is this
            API.keyboardHandler.press(GLFW.GLFW_KEY_SPACE)
            API.keyboardHandler.release(GLFW.GLFW_KEY_SPACE)
            GameInput.jumpKey = true
        } else {
            mc.thePlayer.stopGliding()
        }
    }

    private fun getBoost() =
        when (mode) {
            Mode.CONTROL -> if (GameInput.jumpKey) boostUp
                            else if (GameInput.sneakKey) boostDown
                            else boostGlide
            Mode.KEEP_Y -> if (mc.thePlayer.y < yToKeep) boostUp else 0f
            Mode.HOP -> boostDown
            Mode.JUMP -> boostUp
        }

    enum class Mode {
        CONTROL, KEEP_Y, HOP, JUMP
    }
}