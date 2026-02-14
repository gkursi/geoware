package xyz.qweru.geo.mixin.screen;

import net.minecraft.client.gui.screens.inventory.AbstractMountInventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractMountInventoryScreen.class)
public interface AbstractMountInventoryScreenAccessor {
    @Accessor
    LivingEntity getMount();
}
