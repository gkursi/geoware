package xyz.qweru.geo.mixin.math;

import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Vec2.class)
public interface Vec2fAccessor {
    @Mutable
    @Accessor("x")
    void geo_setX(float x);
    @Mutable
    @Accessor("y")
    void geo_setY(float y);
}
