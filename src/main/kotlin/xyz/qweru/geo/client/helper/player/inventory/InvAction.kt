package xyz.qweru.geo.client.helper.player.inventory

import net.minecraft.world.inventory.ClickType
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import xyz.qweru.geo.client.helper.player.SlotHelper
import xyz.qweru.geo.core.Core.mc
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import xyz.qweru.geo.extend.minecraft.item.isOf

class InvAction(private val type: Type) {

    var from = -1
    var to = -1

    fun from(index: Int): InvAction = fromId(SlotHelper.indexToId(index))

    fun fromId(id: Int): InvAction {
        from = id
        return this
    }

    /**
     * From action
     */
    fun item(predicate: (ItemStack) -> Boolean): InvAction {
        val item = InvHelper.find(predicate)
        return fromId(if (item.found()) item.toId() else -1)
    }

    /**
     * From action
     */
    fun item(item: Item) = item { it.isOf(item) }

    fun to(index: Int): InvAction = toId(SlotHelper.indexToId(index))

    fun toId(id: Int): InvAction {
        to = id
        return this
    }

    fun toOffhand() = toId(45)

    /**
     * Returns false on success, true on fail
     */
    fun apply() = type.block.invoke(this)

    private fun click(id: Int) {
        mc.gameMode!!.handleInventoryMouseClick(
            mc.thePlayer.containerMenu.containerId,
            id, 0, ClickType.PICKUP,
            mc.thePlayer
        )
    }

    enum class Type(val block: InvAction.() -> Boolean) {
        MOVE({
            if (from != -1 && to != -1) {
                click(from)
                click(to)
                click(from)
                false
            } else {
                true
            }
        })
    }
}