package xyz.qweru.geo.mixin.game;

import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Vec3d.class)
public interface Vec3dAccesor {
    @Mutable
    @Accessor("x")
    void geo_setX(double x);
    @Mutable
    @Accessor("y")
    void geo_setY(double y);
    @Mutable
    @Accessor("z")
    void geo_setZ(double z);
}
