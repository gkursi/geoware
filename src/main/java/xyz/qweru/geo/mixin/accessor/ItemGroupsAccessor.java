package xyz.qweru.geo.mixin.accessor;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemGroups.class)
public interface ItemGroupsAccessor {

    @Accessor("INVENTORY")
    static RegistryKey<ItemGroup> geo_getInventory() {
        throw new AssertionError();
    }

}
