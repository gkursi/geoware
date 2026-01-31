package xyz.qweru.geo.client.helper.inventory

import net.minecraft.world.inventory.ClickType
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
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
     * To action
     */
    fun item(item: Item) = item { it.isOf(item) }

    fun to(index: Int): InvAction = toId(SlotHelper.indexToId(index))

    fun toId(id: Int): InvAction {
        to = id
        return this
    }

    fun toOffhand() = toId(45)

    fun toFreeSlot(): InvAction? {
        val inv = mc.thePlayer.inventory
        for (slot in 9..36) {
            if (inv.getItem(slot).isEmpty) return to(slot)
        }
        return null
    }

    /**
     * Returns true on success, false on fail
     */
    fun apply() = type.block.invoke(this)

    private fun click(id: Int, button: Int = 0, type: ClickType = ClickType.PICKUP) {
        mc.gameMode!!.handleInventoryMouseClick(
            mc.thePlayer.containerMenu.containerId,
            id, button, type,
            mc.thePlayer
        )
    }

    enum class Type(val block: InvAction.() -> Boolean) {
        MOVE({
            if (from != -1 && to != -1) {
                click(from)
                click(to)
                click(from)
                true
            } else {
                false
            }
        }),
        QUICK_OFFHAND({
            if (from == -1) false
            else {
                click(from, 40, ClickType.SWAP)
                true
            }
        }),
        PICKUP({
            if (from == -1) false
            else {
                click(from)
                true
            }
        }),
        QUICK_MOVE({
            if (from == -1) false
            else {
                click(from, type = ClickType.QUICK_MOVE)
                true
            }
        })
    }
}