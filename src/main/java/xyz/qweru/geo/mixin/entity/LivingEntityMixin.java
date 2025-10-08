package xyz.qweru.geo.mixin.entity;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.qweru.geo.client.module.combat.ModuleHitbox;
import xyz.qweru.geo.core.Glob;
import xyz.qweru.geo.core.module.Modules;
import xyz.qweru.geo.core.system.Systems;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "getDimensions", at = @At("RETURN"), cancellable = true)
    private void setPlayerHitbox(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        ModuleHitbox hitbox = Systems.Companion.get(Modules.class).get(ModuleHitbox.class);
        if (hitbox.getEnabled() && hitbox.getSize() > 0f && ((Object) this) instanceof PlayerEntity && !this.equals(Glob.mc.player)) {
            EntityDimensions init = cir.getReturnValue();
            cir.setReturnValue(init.scaled(1f + hitbox.getSize(), 1f));
        }
    }

}
