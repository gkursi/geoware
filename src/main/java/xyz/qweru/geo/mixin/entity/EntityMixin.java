package xyz.qweru.geo.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.geo.imixin.IEntity;

@Mixin(Entity.class)
public abstract class EntityMixin implements IEntity {

    @Shadow public abstract boolean isOnGround();

    @Unique int groundTicks = 0;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onEntityTick(CallbackInfo ci) {
        if (isOnGround()) groundTicks++;
        else groundTicks = 0;
    }

    @Override
    public int geo_getGroundTicks() {
        return groundTicks;
    }
}
