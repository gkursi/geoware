package xyz.qweru.geo.mixin.entity;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.qweru.geo.client.module.combat.ModuleReach;
import xyz.qweru.geo.client.module.move.ModuleSafeWalk;
import xyz.qweru.geo.core.system.module.Modules;
import xyz.qweru.geo.core.system.Systems;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Inject(method = "entityInteractionRange", at = @At("RETURN"), cancellable = true)
    private void setEntityReach(CallbackInfoReturnable<Double> cir) {
        double init = cir.getReturnValueD();
        ModuleReach reach = Systems.INSTANCE.get(Modules.class).get(ModuleReach.class);
        cir.setReturnValue(!reach.getEnabled() || init > reach.getEntity() ? init : reach.getEntity());
    }

    @Inject(method = "blockInteractionRange", at = @At("RETURN"), cancellable = true)
    private void setBlockReach(CallbackInfoReturnable<Double> cir) {
        double init = cir.getReturnValueD();
        ModuleReach reach = Systems.INSTANCE.get(Modules.class).get(ModuleReach.class);
        cir.setReturnValue(!reach.getEnabled() || init > reach.getBlock() ? init : reach.getBlock());
    }

    @Inject(method = "isStayingOnGroundSurface", at = @At("RETURN"), cancellable = true)
    private void setClipAtLedge(CallbackInfoReturnable<Boolean> cir) {
        ModuleSafeWalk safeWalk = Systems.INSTANCE.get(Modules.class).get(ModuleSafeWalk.class);
        if (safeWalk.getEnabled() && !safeWalk.getSneak() && safeWalk.check()) {
            cir.setReturnValue(true);
        }
    }

}
