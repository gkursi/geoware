package xyz.qweru.geo.mixin.screen;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreativeModeTabs.class)
public interface ItemGroupsAccessor {

    @Accessor("INVENTORY")
    static ResourceKey<CreativeModeTab> geo_getInventory() {
        throw new AssertionError();
    }

}
