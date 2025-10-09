package xyz.qweru.geo.mixin.accessor;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.HorseScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreativeInventoryScreen.class)
public interface CreativeInventoryScreenAccessor {

    @Accessor("selectedTab")
    static ItemGroup geo_getTab() {
        throw new AssertionError();
    }

}
