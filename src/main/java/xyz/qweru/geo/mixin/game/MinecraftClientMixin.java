package xyz.qweru.geo.mixin.game;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.geo.client.event.PostTickEvent;
import xyz.qweru.geo.client.event.PreTickEvent;
import xyz.qweru.geo.core.event.Events;
import xyz.qweru.geo.core.manager.movement.MovementTicker;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Events.INSTANCE.post(PreTickEvent.INSTANCE);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void postTick(CallbackInfo ci) {
        Events.INSTANCE.post(PostTickEvent.INSTANCE);
    }

    @Inject(
            method = "render",
            at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
                ordinal = 0,
                shift = At.Shift.AFTER
            )
    )
    private void tickMovement(boolean tick, CallbackInfo ci) {
        if (MovementTicker.INSTANCE.getTickSpeed() != 20 && MovementTicker.INSTANCE.canTick()) {
            MovementTicker.INSTANCE.tick();
        }
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"))
    private void cancelEndPacket(ClientPlayNetworkHandler instance, Packet<?> packet, Operation<Void> original) {
        if (MovementTicker.INSTANCE.getTickSpeed() == 20) original.call(instance, packet);
    }
}
