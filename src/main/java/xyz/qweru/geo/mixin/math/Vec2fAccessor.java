package xyz.qweru.geo.mixin.math;

import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Vec2f.class)
public interface Vec2fAccessor {
    @Mutable
    @Accessor("x")
    void geo_setX(float x);
    @Mutable
    @Accessor("y")
    void geo_setY(float y);
}
