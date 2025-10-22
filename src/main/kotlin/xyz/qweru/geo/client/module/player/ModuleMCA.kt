package xyz.qweru.geo.client.module.player

import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import org.lwjgl.glfw.GLFW
import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.thePlayer
import xyz.qweru.geo.client.helper.player.InvHelper
import xyz.qweru.geo.client.helper.timing.TimerDelay
import xyz.qweru.multirender.api.API

class ModuleMCA : Module("MCA", "Bind actions to your middle mouse button", Category.PLAYER) {
    val sg = settings.group("General")
    var groundAction by sg.enum("Ground", "Action to do when middle clicking the ground", Action.PEARL)
    var airAction by sg.enum("Air", "Action to execute while middle clicking air", Action.PEARL)
    var entityAction by sg.enum("Entity", "Action to execute while middle clicking an entity", Action.PEARL)
    var elytraFirework by sg.boolean("Elytra Rocket", "Use rockets while flying with an elytra", true)
    var delay by sg.longRange("Delay", "Delay between actions", 500L..550L, 0L..1000L)

    private val timer = TimerDelay()
    private var doAction = false // if scroll-swapping is enabled, this might take multiple ticks

    @Handler
    private fun onTick(e: PreTickEvent) {
        if (!inGame || (!mc.mouse.wasMiddleButtonClicked() && !doAction)) return
        if (!timer.hasPassed()) return
        val action = if (elytraFirework && mc.thePlayer.isGliding) Action.FIREWORK else when (mc.crosshairTarget) {
            is BlockHitResult -> groundAction
            is EntityHitResult -> entityAction
            else -> airAction
        }
        if (action == Action.NONE) return
        InvHelper.swap(action.item, 0)
        if (!InvHelper.isInMainhand { it.isOf(action.item) } && InvHelper.find({it.isOf(action.item)}).found()) {
            doAction = true
            return
        }
        API.mouseHandler.press(GLFW.GLFW_MOUSE_BUTTON_2)
        API.mouseHandler.release(GLFW.GLFW_MOUSE_BUTTON_2)
        doAction = false
        timer.reset(delay)
    }

    enum class Action(val item: Item) {
        PEARL(Items.ENDER_PEARL),
        EXP(Items.EXPERIENCE_BOTTLE),
        POTION(Items.POTION),
        WIND_CHARGE(Items.WIND_CHARGE),
        FIREWORK(Items.FIREWORK_ROCKET),
        NONE(Items.AIR);
    }
}