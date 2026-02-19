package xyz.qweru.geo.client.helper.inventory

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.animal.camel.Camel
import net.minecraft.world.entity.animal.equine.*
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.AnvilMenu
import net.minecraft.world.inventory.BeaconMenu
import net.minecraft.world.inventory.BlastFurnaceMenu
import net.minecraft.world.inventory.BrewingStandMenu
import net.minecraft.world.inventory.CartographyTableMenu
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.CrafterMenu
import net.minecraft.world.inventory.CraftingMenu
import net.minecraft.world.inventory.DispenserMenu
import net.minecraft.world.inventory.EnchantmentMenu
import net.minecraft.world.inventory.FurnaceMenu
import net.minecraft.world.inventory.GrindstoneMenu
import net.minecraft.world.inventory.HopperMenu
import net.minecraft.world.inventory.HorseInventoryMenu
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.inventory.LecternMenu
import net.minecraft.world.inventory.LoomMenu
import net.minecraft.world.inventory.MerchantMenu
import net.minecraft.world.inventory.ShulkerBoxMenu
import net.minecraft.world.inventory.SmithingMenu
import net.minecraft.world.inventory.SmokerMenu
import net.minecraft.world.inventory.StonecutterMenu
import xyz.qweru.geo.core.Core
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import xyz.qweru.geo.mixin.screen.AbstractMountInventoryScreenAccessor
import xyz.qweru.geo.mixin.screen.CreativeInventoryScreenAccessor
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
        if (Core.mc.player == null) return -1
        val handler: AbstractContainerMenu? = Core.mc.thePlayer.containerMenu

        if (handler is InventoryMenu) return survivalInventory(i)
        if (handler is CreativeModeInventoryScreen.ItemPickerMenu) return creativeInventory(i)
        if (handler is ChestMenu) return genericContainer(i, handler.rowCount)
        if (handler is CraftingMenu) return craftingTable(i)
        if (handler is FurnaceMenu) return furnace(i)
        if (handler is BlastFurnaceMenu) return furnace(i)
        if (handler is SmokerMenu) return furnace(i)
        if (handler is DispenserMenu) return generic3x3(i)
        if (handler is EnchantmentMenu) return enchantmentTable(i)
        if (handler is BrewingStandMenu) return brewingStand(i)
        if (handler is MerchantMenu) return villager(i)
        if (handler is BeaconMenu) return beacon(i)
        if (handler is AnvilMenu) return anvil(i)
        if (handler is HopperMenu) return hopper(i)
        if (handler is ShulkerBoxMenu) return genericContainer(i, 3)
        if (handler is HorseInventoryMenu) return horse(handler, i)
        if (handler is CartographyTableMenu) return cartographyTable(i)
        if (handler is GrindstoneMenu) return grindstone(i)
        if (handler is LecternMenu) return lectern()
        if (handler is LoomMenu) return loom(i)
        if (handler is StonecutterMenu) return stonecutter(i)
        if (handler is CrafterMenu) return crafter(i)
        if (handler is SmithingMenu) return smithingTable(i)

        return -1
    }

    private fun survivalInventory(i: Int): Int {
        if (isHotbar(i)) return 36 + i
        if (isArmor(i)) return 5 + (i - 36)
        if (i == OFFHAND) return 45
        return i
    }

    private fun creativeInventory(i: Int): Int {
        if (CreativeInventoryScreenAccessor.geo_getTab() !== BuiltInRegistries.CREATIVE_MODE_TAB.get(ItemGroupsAccessor.geo_getInventory())) return -1
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

    private fun horse(handler: AbstractContainerMenu, i: Int): Int {
        val entity: AbstractHorse = (handler as AbstractMountInventoryScreenAccessor).mount as AbstractHorse

        if (entity is Llama) {
            val strength = entity.strength
            if (isHotbar(i)) return (2 + 3 * strength) + 28 + i
            if (isMain(i)) return (2 + 3 * strength) + 1 + (i - 9)
        } else if (entity is Horse || entity is SkeletonHorse
            || entity is ZombieHorse || entity is Camel
        ) {
            if (isHotbar(i)) return 29 + i
            if (isMain(i)) return 2 + (i - 9)
        } else if (entity is Donkey) {
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