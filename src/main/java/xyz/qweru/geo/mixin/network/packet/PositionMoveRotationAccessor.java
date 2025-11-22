package xyz.qweru.geo.mixin.network.packet;

import net.minecraft.world.entity.PositionMoveRotation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PositionMoveRotation.class)
public interface PositionMoveRotationAccessor {

    @Mutable
    @Accessor("yRot")
    void geo_setYaw(float pitch);

    @Mutable
    @Accessor("xRot")
    void geo_setPitch(float pitch);

}
