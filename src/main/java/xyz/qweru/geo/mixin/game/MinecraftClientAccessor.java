package xyz.qweru.geo.mixin.game;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MinecraftClientAccessor {
    @Accessor("rightClickDelay")
    void geo_setItemUseCooldown(int itemUseCooldown);
}
