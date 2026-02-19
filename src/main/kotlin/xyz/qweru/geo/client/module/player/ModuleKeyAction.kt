package xyz.qweru.geo.client.module.player

import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import org.lwjgl.glfw.GLFW
import xyz.qweru.geo.client.helper.player.GameOptions
import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.client.helper.entity.TargetHelper
import xyz.qweru.geo.client.helper.network.PacketHelper
import xyz.qweru.geo.client.helper.inventory.InvHelper
import xyz.qweru.geo.client.helper.timing.TimerDelay
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.game.combat.TargetTracker
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.minecraft.entity.inRange
import xyz.qweru.geo.extend.minecraft.game.theLevel
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import xyz.qweru.geo.extend.minecraft.item.isOf
import xyz.qweru.geo.extend.minecraft.world.isOf
import xyz.qweru.multirender.api.API
import xyz.qweru.multirender.api.input.Input

/**
 * this needs major cleanup, maybe separate in to different modules?
 */
class ModuleKeyAction : Module("KeyAction", "Bind actions to keys", Category.PLAYER) {
    val sg = settings.general
    var swapBack by sg.boolean("Swap Back", "Swap back", true)
    var delay by sg.longRange("Delay", "Delay between actions", 250L..275L, 0L..1000L)
    val simulateClick by sg.boolean("Sim Click", "Simulate click", true)
    val silentSwing by sg.boolean("Silent Swing", "Silent swing", true)
        .visible { !simulateClick }

    val smc = settings.group("Middle Click")
    var groundAction by smc.enum("Ground", "Action to do when middle clicking the ground", Action.PEARL)
    var airAction by smc.enum("Air", "Action to execute while middle clicking air", Action.PEARL)
    var entityAction by smc.enum("Entity", "Action to execute while middle clicking an entity", Action.PEARL)
    var elytraFirework by smc.boolean("Elytra Rocket", "Use rockets while flying with an elytra", true)

    val src = settings.group("Ground RClick")
    val swordOnly by src.boolean("Sword Only", "Sword only", true)
    val actionNearEnemy by src.enum("Near Enemy", "Action when near an enemy", Action.OBSIDIAN)
    val enemyRange by src.floatRange("Enemy Range", "Range the enemy has to be from you", 0f..10f, 0f..20f)
    val enemyWallRange by src.floatRange("Wall Range", "Range the enemy has to be from you trough walls", 0f..0f, 0f..20f)
    val onlyTarget by src.boolean("Only Target", "Only check range for the current target", false)
    val otherAction by src.enum("Other", "Action when not near an enemy", Action.FIREBALL)
    val obsidianCrystal by src.boolean("Obsidian Crystal", "Crystal when right clicking obsidian", true)
    val ignoreAnchor by src.boolean("Ignore Anchor", "Don't do the action when holding a respawn anchor", true) // TODO replace with an item filter
    val ignoreObby by src.boolean("Ignore Obby", "Don't do the action when holding obsidian", true) // TODO replace with an item filter

//    val srsw = settings.group("RC Ground")
//    val swordOnly by srsw.boolean("Sword Only")

    private val timer = TimerDelay()
    private var doAction = Action.NONE // if scroll-swapping is enabled, actions might take multiple ticks
    private var swapped = false

    @Handler
    private fun onTick(e: PreTickEvent) {
        if (!inGame) return
        if (!timer.hasPassed()) return

        if (swapped && swapBack) {
            InvHelper.sync()
            swapped = false
            timer.reset(delay)
            mc.thePlayer.displayClientMessage(Component.literal("swap back"), false)
            return
        }

        val action = middleClickAction()
            .takeUnless { it == Action.NONE }
            ?: rightClickAction().takeUnless { it == Action.NONE }
            ?: doAction.takeUnless { it == Action.NONE }
            ?: return

        if (!InvHelper.findInInventory { it.isOf(action.item) }.found()) return
        InvHelper.swap(action.item, 0)
        if (!InvHelper.isInMainhand(action.item)) {
            doAction = action
            return
        }

        mc.thePlayer.displayClientMessage(Component.literal("click"), false)
        click()

        doAction = Action.NONE
        swapped = true
        timer.reset(delay)
    }

    private fun click() {
        if (simulateClick) {
            if (GameOptions.useKey) {
                API.mouseHandler.input(GLFW.GLFW_MOUSE_BUTTON_2, Input.RELEASE)
                API.mouseHandler.input(GLFW.GLFW_MOUSE_BUTTON_2, Input.PRESS)
            } else {
                API.mouseHandler.input(GLFW.GLFW_MOUSE_BUTTON_2, Input.CLICK)
            }
        } else {
            PacketHelper.useItemAndSwing(InteractionHand.MAIN_HAND, silentSwing = silentSwing)
        }
    }

    private fun middleClickAction(): Action {
        if (!mc.mouseHandler.isMiddlePressed) return Action.NONE
        return if (elytraFirework && mc.thePlayer.isFallFlying) Action.FIREWORK else when (mc.hitResult) {
            is BlockHitResult -> groundAction
            is EntityHitResult -> entityAction
            else -> airAction
        }
    }

    private fun rightClickAction(): Action {
        val hit = mc.hitResult
        var action = Action.NONE
        if (!mc.mouseHandler.isRightPressed || hit !is BlockHitResult) return action
        if (InvHelper.getMainhand().item.components().has(DataComponents.FOOD)
            || ignoreAnchor && InvHelper.isHolding(Items.RESPAWN_ANCHOR)) return action
        val state = mc.theLevel.getBlockState(hit.blockPos)

        action = if (obsidianCrystal && !mc.thePlayer.isCrouching && state.isOf(Blocks.OBSIDIAN))
                    Action.CRYSTAL
                else if (state.isAir || ignoreAnchor && state.isOf(Blocks.RESPAWN_ANCHOR) || ignoreObby && state.isOf(Blocks.OBSIDIAN))
                    Action.NONE
                else if (enemyNear())
                    actionNearEnemy else otherAction
        if (!InvHelper.isHolding { InvHelper.isSword(it.item) } && swordOnly && action.block)
            action = Action.NONE
        return action
    }

    private fun enemyNear(): Boolean =
        if (onlyTarget) TargetTracker.target?.inRange(enemyRange) ?: false
        else TargetHelper.findTarget(enemyRange, enemyWallRange, 360f, false) != null

    enum class Action(val item: Item, val block: Boolean = false) {
        PEARL(Items.ENDER_PEARL),
        EXP(Items.EXPERIENCE_BOTTLE),
        POTION(Items.POTION),
        WIND_CHARGE(Items.WIND_CHARGE),
        FIREWORK(Items.FIREWORK_ROCKET),
        FIREBALL(Items.FIRE_CHARGE),
        FLINT_AND_STEEL(Items.FLINT_AND_STEEL),
        OBSIDIAN(Items.OBSIDIAN, block = true),
        WEB(Items.COBWEB, block = true),
        CRYSTAL(Items.END_CRYSTAL),
        NONE(Items.AIR);
    }
}