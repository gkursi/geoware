package xyz.qweru.geo.client.module.move

import net.minecraft.util.math.MathHelper
import org.lwjgl.glfw.GLFW
import xyz.qweru.geo.client.event.VelocityTickEvent
import xyz.qweru.geo.client.helper.input.GameInput
import xyz.qweru.geo.client.helper.timing.Timer
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.airTicks
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

    var wasGliding = false
    var glideTimer = Timer()

    @Handler
    private fun onVelocity(e: VelocityTickEvent) {
        if (!shouldFly(e)) return
        val gliding = mc.thePlayer.isGliding
        val canGlide = mc.thePlayer.canGlide
        val onGround = mc.thePlayer.isOnGround

        if (canGlide) GameInput.jumpKey = false

        if (bounce && onGround && gliding) {
            API.keyboardHandler.press(GLFW.GLFW_KEY_SPACE)
            API.keyboardHandler.release(GLFW.GLFW_KEY_SPACE)
        }

        if (gliding && !wasGliding) {
            e.y += getBoost()
            if (GameInput.moving) {
                val hvec = mc.thePlayer.getRotationVector(0f, mc.thePlayer.yaw).multiply(hBoost.toDouble())
                e.x += hvec.x
                e.z += hvec.z
            }
        }

        if (e.y < 0 && canGlide && gliding) {
            glide(false)
            glideTimer.reset()
        }

        if (!gliding && canGlide) {
            glide(true)
            logger.info("Starting glide")
        }

        if (gliding) {
            val mul = if (GameInput.moving) hMul else 1f
            e.x = MathHelper.clamp(e.x.toFloat() * mul, -hBoostLimit, hBoostLimit).toDouble()
            e.z = MathHelper.clamp(e.z.toFloat() * mul, -hBoostLimit, hBoostLimit).toDouble()
        }

        wasGliding = gliding
    }

    private fun shouldFly(e: VelocityTickEvent): Boolean =
        when (mode) {
            Mode.CONTROL -> true
            Mode.JUMP -> mc.thePlayer.airTicks > 0 && mc.thePlayer.airTicks < 10
            Mode.KEEP_Y -> mc.thePlayer.y < yToKeep
        }

    private fun glide(b: Boolean) {
        if (b) {
            API.keyboardHandler.press(GLFW.GLFW_KEY_SPACE)
            API.keyboardHandler.release(GLFW.GLFW_KEY_SPACE)
            GameInput.jumpKey = true
        } else {
            mc.thePlayer.stopGliding()
        }
    }

    private fun getBoost() =
        if (GameInput.jumpKey || (mode == Mode.JUMP && mc.thePlayer.airTicks < 4) || (mode == Mode.KEEP_Y && mc.thePlayer.y < yToKeep)) boostUp
        else if (GameInput.sneakKey) boostDown
        else boostGlide

    enum class Mode {
        CONTROL, JUMP, KEEP_Y
    }
}