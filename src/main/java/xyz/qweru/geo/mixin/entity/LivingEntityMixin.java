package xyz.qweru.geo.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.qweru.geo.client.event.TravelEvent;
import xyz.qweru.geo.client.module.combat.ModuleHitbox;
import xyz.qweru.geo.client.module.visual.ModuleViewModel;
import xyz.qweru.geo.core.Core;
import xyz.qweru.geo.core.event.EventBus;
import xyz.qweru.geo.core.system.module.Modules;
import xyz.qweru.geo.core.system.Systems;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "getDimensions", at = @At("RETURN"), cancellable = true)
    private void setPlayerHitbox(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        ModuleHitbox hitbox = Systems.INSTANCE.get(Modules.class).get(ModuleHitbox.class);
        if (hitbox.getEnabled() && hitbox.getSize() > 0f && ((Object) this) instanceof Player && !this.equals(Core.mc.player)) {
            EntityDimensions init = cir.getReturnValue();
            cir.setReturnValue(init.scale(1f + hitbox.getSize(), 1f));
        }
    }

    @WrapMethod(method = "getCurrentSwingDuration")
    private int getHandSwingDuration(Operation<Integer> original) {
        ModuleViewModel vm = Systems.INSTANCE.get(Modules.class).get(ModuleViewModel.class);
        return (int) (original.call() * (vm.getEnabled() ? 1f / vm.getSwingSpeed() : 1f));
    }

    @Inject(method = "travel", at = @At("HEAD"))
    private void preTravel(Vec3 vec3, CallbackInfo ci) {
        TravelEvent.INSTANCE.setVec(vec3);
        EventBus.INSTANCE.post(TravelEvent.INSTANCE);
    }

}
