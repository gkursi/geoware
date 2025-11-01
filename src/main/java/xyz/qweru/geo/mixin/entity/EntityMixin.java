package xyz.qweru.geo.mixin.entity;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.qweru.geo.imixin.IClientPlayerEntity;

import static xyz.qweru.geo.core.Global.mc;

@Mixin(Entity.class)
public class EntityMixin {
//    @Inject(method = "setSprinting", at = @At("HEAD"))
//    private void set(boolean sprinting, CallbackInfo ci) {
//        if (this.equals(mc.player)) {
//            new Throwable().printStackTrace();
//        }
//    }
}
