package xyz.qweru.geo.mixin.screen;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreativeModeInventoryScreen.class)
public interface CreativeInventoryScreenAccessor {

    @Accessor("selectedTab")
    static CreativeModeTab geo_getTab() {
        throw new AssertionError();
    }

}
