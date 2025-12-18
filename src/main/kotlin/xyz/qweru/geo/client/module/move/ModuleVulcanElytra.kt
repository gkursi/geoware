package xyz.qweru.geo.client.module.move

import net.minecraft.util.Mth
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.Items
import org.lwjgl.glfw.GLFW
import xyz.qweru.geo.client.event.PostCrosshair
import xyz.qweru.geo.client.event.PostMovementTickEvent
import xyz.qweru.geo.client.helper.player.GameOptions
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.game.movement.MovementTicker
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.minecraft.entity.canGlide
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import xyz.qweru.geo.extend.minecraft.item.isOf
import xyz.qweru.multirender.api.API
import xyz.qweru.multirender.api.input.Input

class ModuleVulcanElytra : Module("VulcanElytra","Fly without rockets", Category.MOVEMENT) {
    val sg = settings.group("General")
    val mode by sg.enum("Mode", "Mode", Mode.CONTROL)
    val bounce by sg.boolean("Bounce", "Should you bounce off the ground", false)
    val yToKeep by sg.float("Keep Y", "Which y coord to stay above", 320f, -90f, 600f)
        .visible { mode == Mode.KEEP_Y }
    val moveTick by sg.int("Move Tick", "Move tick speed", 19, 1, 20)

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
    private fun crosshair(e: PostCrosshair) {
        MovementTicker.tickSpeed = moveTick
    }

    @Handler
    private fun onVelocity(event: PostMovementTickEvent) {
        GameOptions.syncBind(GameOptions::jumpKey)
        if (!shouldFly(event)) return

        gliding = mc.thePlayer.isFallFlying
        canGlide = mc.thePlayer.canGlide
        onGround = mc.thePlayer.onGround()

        if (canGlide) GameOptions.jumpKey = false

        if (shouldBounce()) mc.thePlayer.jumpFromGround()

        if (gliding && !wasGliding) {
            event.velY += getBoost()
            if (GameOptions.moving) {
                var yaw = Mth.wrapDegrees(mc.thePlayer.yRot)
                if (hControl) {
                    if (GameOptions.backKey) yaw -= 180
                    if (GameOptions.leftKey) yaw -= 90
                    if (GameOptions.rightKey) yaw += 90
                }
                val hvec = mc.thePlayer.calculateViewVector(0f, yaw).scale(hBoost.toDouble())
                event.velX += hvec.x
                event.velZ += hvec.z
            }
        }


        if (!gliding && canGlide) glide(true)
        else if (shouldStopGlide(event)) glide(false)

        if (gliding) {
            val mul = if (GameOptions.moving) hMul else 1f
            event.velX *= mul
            event.velZ *= mul
            event.clampHorizontal(hBoostLimit.toDouble())
        }

        wasGliding = gliding
    }

    private fun shouldStopGlide(e: PostMovementTickEvent): Boolean =
        ((e.velY < 0 && canGlide) || (mode == Mode.HOP)) && gliding && mode != Mode.JUMP

    private fun shouldBounce(): Boolean =
        (bounce && onGround && gliding)
        || (onGround && !gliding && mode == Mode.HOP
            && mc.thePlayer.getItemBySlot(EquipmentSlot.CHEST).isOf(Items.ELYTRA))
    
    private fun shouldFly(e: PostMovementTickEvent): Boolean =
        when (mode) {
            Mode.CONTROL, Mode.HOP -> true
            Mode.KEEP_Y -> mc.thePlayer.y < yToKeep
            Mode.JUMP -> true
        }

    private fun glide(b: Boolean) {
        if (b) {
            // TODO the fuck is this
            API.keyboardHandler.input(GLFW.GLFW_KEY_SPACE, Input.CLICK)
            GameOptions.jumpKey = true
        } else {
            mc.thePlayer.stopFallFlying()
        }
    }

    private fun getBoost() =
        when (mode) {
            Mode.CONTROL -> if (GameOptions.jumpKey) boostUp
                            else if (GameOptions.sneakKey) boostDown
                            else boostGlide
            Mode.KEEP_Y -> if (mc.thePlayer.y < yToKeep) boostUp else 0f
            Mode.HOP -> boostDown
            Mode.JUMP -> boostUp
        }

    enum class Mode {
        CONTROL, KEEP_Y, HOP, JUMP
    }
}