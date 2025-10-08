package xyz.qweru.geo.mixin.entity;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityType.class)
public class EntityTypeMixin {

    @Inject(method = "getDimensions", at = @At("RETURN"), cancellable = true)
    private void changeHitbox(CallbackInfoReturnable<EntityDimensions> cir) {

    }

}
