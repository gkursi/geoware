package xyz.qweru.geo.client.module.move

import net.minecraft.client.option.GameOptions
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.Items
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
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
    val hControl by sb.boolean("H Control", "Horizontal boost control", false)

    var wasGliding = false
    var glideTimer = Timer()

    @Handler
    private fun onVelocity(e: VelocityTickEvent) {
        mc.options.jumpKey.isPressed = GameInput.jumpKey
        if (!shouldFly(e)) return
        val gliding = mc.thePlayer.isGliding
        val canGlide = mc.thePlayer.canGlide
        val onGround = mc.thePlayer.isOnGround

        if (canGlide) GameInput.jumpKey = false

        if ((bounce && onGround && gliding) || (onGround && mc.thePlayer.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA) && !gliding && mode == Mode.HOP)) {
            mc.thePlayer.jump()
        }

        if (gliding && !wasGliding) {
            e.y += getBoost()
            if (GameInput.moving) {
                var yaw = MathHelper.wrapDegrees(mc.thePlayer.yaw)
                if (GameInput.backKey) yaw -= 180
                if (GameInput.leftKey) yaw -= 90
                if (GameInput.rightKey) yaw += 90
                val hvec = mc.thePlayer.getRotationVector(0f, yaw).multiply(hBoost.toDouble())
                e.x += hvec.x
                e.z += hvec.z
            }
        }

        if (((e.y < 0 && canGlide) || (mode == Mode.HOP)) && gliding) {
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
            Mode.CONTROL, Mode.HOP -> true
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
        when (mode) {
            Mode.CONTROL -> if (GameInput.jumpKey) boostUp
                            else if (GameInput.sneakKey) boostDown
                            else boostGlide
            Mode.JUMP -> if (mc.thePlayer.airTicks < 4) boostUp else 0f
            Mode.KEEP_Y -> if (mc.thePlayer.y < yToKeep) boostUp else 0f
            Mode.HOP -> boostDown
        }

    enum class Mode {
        CONTROL, JUMP, KEEP_Y, HOP
    }
}