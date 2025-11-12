package xyz.qweru.geo.mixin.world;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.entity.EntityTickList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientLevel.class)
public interface ClientWorldAccessor {
    @Accessor("tickingEntities")
    EntityTickList geo_getEntityList();
}
