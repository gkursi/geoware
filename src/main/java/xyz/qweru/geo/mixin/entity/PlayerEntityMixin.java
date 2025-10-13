package xyz.qweru.geo.mixin.entity;

import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.qweru.geo.client.module.combat.ModuleReach;
import xyz.qweru.geo.client.module.move.ModuleSafeWalk;
import xyz.qweru.geo.core.system.module.Modules;
import xyz.qweru.geo.core.system.Systems;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Shadow public abstract void startGliding();

    @Inject(method = "getEntityInteractionRange", at = @At("RETURN"), cancellable = true)
    private void setEntityReach(CallbackInfoReturnable<Double> cir) {
        double init = cir.getReturnValueD();
        ModuleReach reach = Systems.Companion.get(Modules.class).get(ModuleReach.class);
        cir.setReturnValue(!reach.getEnabled() || init > reach.getEntity() ? init : reach.getEntity());
    }

    @Inject(method = "getBlockInteractionRange", at = @At("RETURN"), cancellable = true)
    private void setBlockReach(CallbackInfoReturnable<Double> cir) {
        double init = cir.getReturnValueD();
        ModuleReach reach = Systems.Companion.get(Modules.class).get(ModuleReach.class);
        cir.setReturnValue(!reach.getEnabled() || init > reach.getBlock() ? init : reach.getBlock());
    }

    @Unique double moveX = 0d, moveZ = 0d;
    @Inject(method = "adjustMovementForSneaking", at = @At("HEAD"))
    private void getMovement(Vec3d movement, MovementType type, CallbackInfoReturnable<Vec3d> cir) {
        moveX = movement.x;
        moveZ = movement.z;
    }

    @Inject(method = "clipAtLedge", at = @At("RETURN"), cancellable = true)
    private void setClipAtLedge(CallbackInfoReturnable<Boolean> cir) {
        ModuleSafeWalk safeWalk = Systems.Companion.get(Modules.class).get(ModuleSafeWalk.class);
        if (safeWalk.getEnabled()) {
            if (!safeWalk.getSneak() && safeWalk.check()) cir.setReturnValue(true);
        }
    }

}
