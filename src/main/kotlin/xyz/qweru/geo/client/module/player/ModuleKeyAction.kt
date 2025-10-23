package xyz.qweru.geo.client.module.player

import net.minecraft.block.Blocks
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import org.lwjgl.glfw.GLFW
import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.client.helper.entity.TargetHelper
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.thePlayer
import xyz.qweru.geo.client.helper.player.InvHelper
import xyz.qweru.geo.client.helper.timing.TimerDelay
import xyz.qweru.geo.core.manager.combat.TargetTracker
import xyz.qweru.geo.extend.inRange
import xyz.qweru.geo.extend.theWorld
import xyz.qweru.multirender.api.API

class ModuleKeyAction : Module("KeyAction", "Bind actions to keys", Category.PLAYER) {
    val smc = settings.group("Middle Click")
    var groundAction by smc.enum("Ground", "Action to do when middle clicking the ground", Action.PEARL)
    var airAction by smc.enum("Air", "Action to execute while middle clicking air", Action.PEARL)
    var entityAction by smc.enum("Entity", "Action to execute while middle clicking an entity", Action.PEARL)
    var elytraFirework by smc.boolean("Elytra Rocket", "Use rockets while flying with an elytra", true)
    var delay by smc.longRange("Delay", "Delay between actions", 500L..550L, 0L..1000L)

    val src = settings.group("Ground RClick")
    val actionNearEnemy by src.enum("Near Enemy", "Action when near an enemy", Action.OBSIDIAN)
    val enemyRange by src.floatRange("Enemy Range", "Range the enemy has to be from you", 0f..10f, 0f..20f)
    val onlyTarget by src.boolean("Only Target", "Only check range for the current target", false)
    val otherAction by src.enum("Other", "Action when not near an enemy", Action.FIREBALL)
    val obsidianCrystal by src.boolean("Obsidian Crystal", "Crystal when right clicking obsidian", true)

//    val srsw = settings.group("RC Ground")
//    val swordOnly by srsw.boolean("Sword Only")

    private val timer = TimerDelay()
    private var doAction = Action.NONE // if scroll-swapping is enabled, actions might take multiple ticks

    @Handler
    private fun onTick(e: PreTickEvent) {
        if (!inGame) return
        if (!timer.hasPassed()) return

        val action = middleClickAction()
            .takeUnless { it == Action.NONE }
            ?: rightClickAction().takeUnless { it == Action.NONE }
            ?: doAction.takeUnless { it == Action.NONE }
            ?: return
        logger.info("Action: $action")
        if (!InvHelper.find({ it.isOf(action.item) }).found()) return
        InvHelper.swap(action.item, 0)
        if (!InvHelper.isInMainhand(action.item)) {
            doAction = action
            return
        }

        if (mc.options.useKey.isPressed) {
            API.mouseHandler.release(GLFW.GLFW_MOUSE_BUTTON_2)
            API.mouseHandler.press(GLFW.GLFW_MOUSE_BUTTON_2)
        } else {
            API.mouseHandler.press(GLFW.GLFW_MOUSE_BUTTON_2)
            API.mouseHandler.release(GLFW.GLFW_MOUSE_BUTTON_2)
        }

        doAction = Action.NONE
        timer.reset(delay)
    }

    private fun middleClickAction(): Action {
        if (!mc.mouse.wasMiddleButtonClicked()) return Action.NONE
        return if (elytraFirework && mc.thePlayer.isGliding) Action.FIREWORK else when (mc.crosshairTarget) {
            is BlockHitResult -> groundAction
            is EntityHitResult -> entityAction
            else -> airAction
        }
    }

    private fun rightClickAction(): Action {
        val hit = mc.crosshairTarget
        if (!mc.mouse.wasRightButtonClicked() || hit !is BlockHitResult) return Action.NONE
        val state = mc.theWorld.getBlockState(hit.blockPos)
        if (state.isAir) return Action.NONE

        if (obsidianCrystal && !mc.thePlayer.isSneaking && state.isOf(Blocks.OBSIDIAN))
            return Action.CRYSTAL
        return if (enemyNear()) actionNearEnemy else otherAction
    }

    private fun enemyNear(): Boolean =
        if (onlyTarget) TargetTracker.target?.inRange(enemyRange) ?: false
        else TargetHelper.findTarget(enemyRange, 360f, false) != null

    enum class Action(val item: Item) {
        PEARL(Items.ENDER_PEARL),
        EXP(Items.EXPERIENCE_BOTTLE),
        POTION(Items.POTION),
        WIND_CHARGE(Items.WIND_CHARGE),
        FIREWORK(Items.FIREWORK_ROCKET),
        FIREBALL(Items.FIRE_CHARGE),
        FLINT_AND_STEEL(Items.FLINT_AND_STEEL),
        OBSIDIAN(Items.OBSIDIAN),
        WEB(Items.COBWEB),
        CRYSTAL(Items.END_CRYSTAL),
        NONE(Items.AIR);

    }
}