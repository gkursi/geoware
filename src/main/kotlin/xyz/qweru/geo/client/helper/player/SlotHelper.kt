package xyz.qweru.geo.client.helper.player

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen
import net.minecraft.client.network.ClientPlayerInteractionManager
import net.minecraft.entity.mob.SkeletonHorseEntity
import net.minecraft.entity.mob.ZombieHorseEntity
import net.minecraft.entity.passive.*
import net.minecraft.registry.Registries
import net.minecraft.screen.*
import xyz.qweru.geo.core.Global.mc
import xyz.qweru.geo.extend.thePlayer
import xyz.qweru.geo.mixin.screen.CreativeInventoryScreenAccessor
import xyz.qweru.geo.mixin.screen.HorseScreenHandlerAccessor
import xyz.qweru.geo.mixin.screen.ItemGroupsAccessor


object SlotHelper {
    /**
     * These constants refer to the slot index of relevant player slots. They are used when dealing directly with the
     * player inventory - e.g. `mc.thePlayer.getInventory().getSelectedSlot()` returns the slot index of your
     * selected slot (i.e. main hand).
     *
     * @see net.minecraft.entity.player.PlayerInventory
     *
     * @see Slot.index
     */
    const val HOTBAR_START: Int = 0
    const val HOTBAR_END: Int = 8
    const val MAIN_START: Int = 9
    const val MAIN_END: Int = 35
    const val ARMOR_START: Int = 36
    const val ARMOR_END: Int = 39
    const val OFFHAND: Int = 40

    /**
     * Slot ids are used when inventory interactions have to be communicated to the server - you'll only find references
     * to slot ids when dealing with screen handlers or slot/inventory packets. All the methods in this class are used
     * to translate slot indices to the ids for each handled screen.
     *
     * @see [the minecraft.wiki page](https://minecraft.wiki/w/Java_Edition_protocol/Inventory) for every slot id
     *
     * @see ClientPlayerInteractionManager.clickSlot
     * @see ScreenHandler.internalOnSlotClick
     * @see net.minecraft.screen.slot.Slot.id
     */
    fun indexToId(i: Int): Int {
        if (mc.player == null) return -1
        val handler: ScreenHandler? = mc.thePlayer.currentScreenHandler

        if (handler is PlayerScreenHandler) return survivalInventory(i)
        if (handler is CreativeInventoryScreen.CreativeScreenHandler) return creativeInventory(i)
        if (handler is GenericContainerScreenHandler) return genericContainer(i, handler.getRows())
        if (handler is CraftingScreenHandler) return craftingTable(i)
        if (handler is FurnaceScreenHandler) return furnace(i)
        if (handler is BlastFurnaceScreenHandler) return furnace(i)
        if (handler is SmokerScreenHandler) return furnace(i)
        if (handler is Generic3x3ContainerScreenHandler) return generic3x3(i)
        if (handler is EnchantmentScreenHandler) return enchantmentTable(i)
        if (handler is BrewingStandScreenHandler) return brewingStand(i)
        if (handler is MerchantScreenHandler) return villager(i)
        if (handler is BeaconScreenHandler) return beacon(i)
        if (handler is AnvilScreenHandler) return anvil(i)
        if (handler is HopperScreenHandler) return hopper(i)
        if (handler is ShulkerBoxScreenHandler) return genericContainer(i, 3)
        if (handler is HorseScreenHandler) return horse(handler, i)
        if (handler is CartographyTableScreenHandler) return cartographyTable(i)
        if (handler is GrindstoneScreenHandler) return grindstone(i)
        if (handler is LecternScreenHandler) return lectern()
        if (handler is LoomScreenHandler) return loom(i)
        if (handler is StonecutterScreenHandler) return stonecutter(i)
        if (handler is CrafterScreenHandler) return crafter(i)
        if (handler is SmithingScreenHandler) return smithingTable(i)

        return -1
    }

    private fun survivalInventory(i: Int): Int {
        if (isHotbar(i)) return 36 + i
        if (isArmor(i)) return 5 + (i - 36)
        if (i == OFFHAND) return 45
        return i
    }

    private fun creativeInventory(i: Int): Int {
        if (CreativeInventoryScreenAccessor.geo_getTab() !== Registries.ITEM_GROUP.get(ItemGroupsAccessor.geo_getInventory())) return -1
        return survivalInventory(i)
    }

    private fun genericContainer(i: Int, rows: Int): Int {
        if (isHotbar(i)) return (rows + 3) * 9 + i
        if (isMain(i)) return rows * 9 + (i - 9)
        return -1
    }

    private fun craftingTable(i: Int): Int {
        if (isHotbar(i)) return 37 + i
        if (isMain(i)) return i + 1
        return -1
    }

    private fun furnace(i: Int): Int {
        if (isHotbar(i)) return 30 + i
        if (isMain(i)) return 3 + (i - 9)
        return -1
    }

    private fun generic3x3(i: Int): Int {
        if (isHotbar(i)) return 36 + i
        if (isMain(i)) return i
        return -1
    }

    private fun enchantmentTable(i: Int): Int {
        if (isHotbar(i)) return 29 + i
        if (isMain(i)) return 2 + (i - 9)
        return -1
    }

    private fun brewingStand(i: Int): Int {
        if (isHotbar(i)) return 32 + i
        if (isMain(i)) return 5 + (i - 9)
        return -1
    }

    private fun villager(i: Int): Int {
        if (isHotbar(i)) return 30 + i
        if (isMain(i)) return 3 + (i - 9)
        return -1
    }

    private fun beacon(i: Int): Int {
        if (isHotbar(i)) return 28 + i
        if (isMain(i)) return 1 + (i - 9)
        return -1
    }

    private fun anvil(i: Int): Int {
        if (isHotbar(i)) return 30 + i
        if (isMain(i)) return 3 + (i - 9)
        return -1
    }

    private fun hopper(i: Int): Int {
        if (isHotbar(i)) return 32 + i
        if (isMain(i)) return 5 + (i - 9)
        return -1
    }

    private fun horse(handler: ScreenHandler, i: Int): Int {
        val entity: AbstractHorseEntity? = (handler as HorseScreenHandlerAccessor).geo_getEntity()

        if (entity is LlamaEntity) {
            val strength = entity.getStrength()
            if (isHotbar(i)) return (2 + 3 * strength) + 28 + i
            if (isMain(i)) return (2 + 3 * strength) + 1 + (i - 9)
        } else if (entity is HorseEntity || entity is SkeletonHorseEntity
            || entity is ZombieHorseEntity || entity is CamelEntity
        ) {
            if (isHotbar(i)) return 29 + i
            if (isMain(i)) return 2 + (i - 9)
        } else if (entity is AbstractDonkeyEntity) {
            val chest = entity.hasChest()
            if (isHotbar(i)) return (if (chest) 44 else 29) + i
            if (isMain(i)) return (if (chest) 17 else 2) + (i - 9)
        }

        return -1
    }

    private fun cartographyTable(i: Int): Int {
        if (isHotbar(i)) return 30 + i
        if (isMain(i)) return 3 + (i - 9)
        return -1
    }

    private fun grindstone(i: Int): Int {
        if (isHotbar(i)) return 30 + i
        if (isMain(i)) return 3 + (i - 9)
        return -1
    }

    private fun lectern(): Int {
        return -1
    }

    private fun loom(i: Int): Int {
        if (isHotbar(i)) return 31 + i
        if (isMain(i)) return 4 + (i - 9)
        return -1
    }

    private fun stonecutter(i: Int): Int {
        if (isHotbar(i)) return 29 + i
        if (isMain(i)) return 2 + (i - 9)
        return -1
    }

    private fun crafter(i: Int): Int {
        if (isHotbar(i)) return 36 + i
        if (isMain(i)) return i
        return -1
    }

    private fun smithingTable(i: Int): Int {
        if (isHotbar(i)) return 31 + i
        if (isMain(i)) return 4 + (i - 9)
        return -1
    }

    // Utils
    fun isHotbar(slotIndex: Int): Boolean {
        return slotIndex >= HOTBAR_START && slotIndex <= HOTBAR_END
    }

    fun isMain(slotIndex: Int): Boolean {
        return slotIndex >= MAIN_START && slotIndex <= MAIN_END
    }

    fun isArmor(slotIndex: Int): Boolean {
        return slotIndex >= ARMOR_START && slotIndex <= ARMOR_END
    }
}