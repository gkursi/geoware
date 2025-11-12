package xyz.qweru.geo.mixin.screen;

import net.minecraft.client.gui.screens.inventory.HorseInventoryScreen;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HorseInventoryScreen.class)
public interface HorseScreenHandlerAccessor {

    @Accessor("horse")
    AbstractHorse geo_getEntity();

}
