package xyz.qweru.geo.client.module.combat

import net.minecraft.client.gui.screens.inventory.InventoryScreen
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket
import net.minecraft.world.item.Items
import org.lwjgl.glfw.GLFW
import xyz.qweru.geo.client.event.PacketReceiveEvent
import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import xyz.qweru.geo.extend.minecraft.game.theLevel
import xyz.qweru.geo.client.helper.inventory.InvHelper
import xyz.qweru.geo.client.helper.timing.TimerDelay
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.extend.minecraft.item.isOf
import xyz.qweru.geo.mixin.screen.HandledScreenAccessor
import xyz.qweru.multirender.api.API
import xyz.qweru.multirender.api.input.Input

class ModuleAutoTotem : Module("AutoTotem", "Automatically use totems", Category.COMBAT) {
    val sg = settings.general
    val sdh = settings.group("Double Hand")

//    var silent by sg.boolean("Silent Inv", "Open inventory silently", false)

    var openInv by sg.boolean("Open Inv", "Opens the inventory on pop", true)
    var openDelay by sg.longRange("Open Delay", "How long to wait before opening the inventory", 0L..10L, 0L..500L)
    var autoSwap by sg.boolean("Auto Swap", "Swap automatically", true)
    var swapTime by sg.longRange("Swap Delay", "How long to hover a totem before offhanding", 10L..50L, 0L..1000L)
    var swapMode by sg.enum("Swap Mode", "Mode for triggering swap", Mode.PACKET)
    var closeInv by sg.boolean("Close Inv", "Closes the inventory after equipping a totem", true)
    var closeDelay by sg.longRange("Open Delay", "How long to wait before closing the inventory", 0L..10L, 0L..500L)

    var doubleHand by sdh.boolean("Double-hand", "Auto double-hand on pop", true)
    var instantDh by sdh.boolean("DH Instant", "Instantly trigger double-hand", true)
    var dhDelay by sdh.longRange("DH Delay", "Double hand delay", 0L..50L, 0L..500L).visible { !instantDh }
    var pauseScreen by sdh.boolean("DH Pause Screen", "Pause double hand on screen", false)

    private var finishSwap = false
    private val dhTimer = TimerDelay()

    private var readyToSwap = false
    private val swapTimer = TimerDelay()
    private var openScreen = false
    private val openTimer = TimerDelay()
    private var closeScreen = true
    private val closeTimer = TimerDelay()

    @Handler
    private fun onPacket(e: PacketReceiveEvent) {
        val packet = e.packet
        if (packet !is ClientboundEntityEventPacket) return
        if (packet.eventId != 35.toByte() || mc.player != packet.getEntity(mc.theLevel)) return

        openScreen = openInv
        openTimer.reset(openDelay)

        if (!doubleHand) return
        if (instantDh) {
            InvHelper.swap(Items.TOTEM_OF_UNDYING, 1000)
            finishSwap = !InvHelper.isInMainhand(Items.TOTEM_OF_UNDYING)
        } else {
            finishSwap = true
            dhTimer.reset(dhDelay)
        }
    }

    @Handler
    private fun onTick(e: PreTickEvent) {
        if (!inGame) return
        if (finishSwap && dhTimer.hasPassed()) {
            if (pauseScreen && mc.screen != null) return
            InvHelper.swap(Items.TOTEM_OF_UNDYING, 1000)
            finishSwap = !InvHelper.isInMainhand(Items.TOTEM_OF_UNDYING)
            if (!finishSwap && !instantDh) dhTimer.reset(dhDelay)
        }
        if (InvHelper.isInOffhand(Items.TOTEM_OF_UNDYING)) {
            if (closeScreen && closeTimer.hasPassed()) {
                mc.thePlayer.closeContainer()
                closeScreen = false
            }
            return
        }

        if (mc.screen is InventoryScreen) {
            if (!autoSwap) return
            swap(when (swapMode) {
                Mode.HOVER -> {
                    val slot = (mc.screen as HandledScreenAccessor).geo_getFocusedSlot() ?: return
                    val stack = slot.item
                    if (!stack.isOf(Items.TOTEM_OF_UNDYING)) return
                    slot.containerSlot
                }
                Mode.PACKET -> {
                    val slot = InvHelper.find({ it.isOf(Items.TOTEM_OF_UNDYING) }, 9, 36)
                    if (!slot.found()) return
                    slot.toId()
                }
            })
        } else if (openScreen && openTimer.hasPassed()) {
//            mc.setScreen(InventoryScreen(mc.player))
            API.keyboardHandler.input(GLFW.GLFW_KEY_E, Input.CLICK)
            openScreen = false
        }
    }

    private fun swap(id: Int) {
        if (!readyToSwap) {
            readyToSwap = true
            swapTimer.reset(swapTime)
        }
        if (!swapTimer.hasPassed()) return
        InvHelper.offhand().fromId(id).apply()
        closeScreen = closeInv
        closeTimer.reset(closeDelay)
        readyToSwap = false
    }

    enum class Mode {
        HOVER, PACKET
    }
}