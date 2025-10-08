package xyz.qweru.geo.client.module.player

import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import org.lwjgl.glfw.GLFW
import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.module.Module
import xyz.qweru.geo.helper.player.HotbarHelper
import xyz.qweru.geo.helper.timing.TimerDelay
import xyz.qweru.multirender.api.API

class ModuleMCA : Module("MCA", "Bind actions to your middle mouse button") {
    val sg = settings.group("General")
    var groundAction by sg.enum("Ground", "Action to do when middle clicking the ground", Action.PEARL)
    var airAction by sg.enum("Air", "Action to execute while middle clicking air", Action.PEARL)
    var entityAction by sg.enum("Entity", "Action to execute while middle clicking an entity", Action.PEARL)
    var delay by sg.delay("Delay", "Delay between actions", 500, 550, 0, 1000)

    private val timer = TimerDelay()
    private var doAction = false // if scroll-swapping is enabled, this might take multiple ticks

    @Handler
    private fun onTick(e: PreTickEvent) {
        if (!inGame || (!mc.mouse.wasMiddleButtonClicked() && !doAction)) return
        if (!timer.hasPassed()) return
        val action = when(mc.crosshairTarget) {
            is BlockHitResult -> groundAction
            is EntityHitResult -> entityAction
            null -> Action.NONE
            else -> airAction
        }
        if (action == Action.NONE) return
        HotbarHelper.swap(action.item, 0)
        if (!HotbarHelper.isInMainhand {it.isOf(action.item)}) {
            doAction = true
            return
        }
        API.mouseHandler.press(GLFW.GLFW_MOUSE_BUTTON_2)
        API.mouseHandler.release(GLFW.GLFW_MOUSE_BUTTON_2)
        doAction = false
        timer.reset(delay.min, delay.max)
    }

    enum class Action(val item: Item) {
        PEARL(Items.ENDER_PEARL),
        EXP(Items.EXPERIENCE_BOTTLE),
        POTION(Items.POTION),
        NONE(Items.AIR);
    }
}