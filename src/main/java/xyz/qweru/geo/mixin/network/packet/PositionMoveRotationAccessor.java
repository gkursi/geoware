package xyz.qweru.geo.mixin.network.packet;

import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.phys.Vec3;
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

    @Mutable
    @Accessor("deltaMovement")
    void geo_setDelta(Vec3 delta);

    @Mutable
    @Accessor("position")
    void geo_setPos(Vec3 delta);
}
