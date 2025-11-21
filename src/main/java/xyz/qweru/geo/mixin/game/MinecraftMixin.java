package xyz.qweru.geo.mixin.game;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.geo.client.event.*;
import xyz.qweru.geo.core.event.EventBus;
import xyz.qweru.geo.core.game.movement.MovementTicker;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        EventBus.INSTANCE.post(PreTickEvent.INSTANCE);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void postTick(CallbackInfo ci) {
        EventBus.INSTANCE.post(PostTickEvent.INSTANCE);
    }

    @Inject(
            method = "runTick",
            at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V",
                ordinal = 0,
                shift = At.Shift.AFTER
            )
    )
    private void tickMovement(boolean tick, CallbackInfo ci) {
        if (MovementTicker.INSTANCE.getTickSpeed() != 20 && MovementTicker.INSTANCE.canTick()) {
            MovementTicker.INSTANCE.tick();
        }
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V"))
    private void cancelEndPacket(ClientPacketListener instance, Packet<?> packet, Operation<Void> original) {
        if (MovementTicker.INSTANCE.getTickSpeed() == 20) original.call(instance, packet);
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;runAllTasks()V", shift = At.Shift.BEFORE))
    private void preRunTasks(boolean tick, CallbackInfo ci) {
        EventBus.INSTANCE.post(HandleTasksEvent.INSTANCE);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void postInit(GameConfig gameConfig, CallbackInfo ci) {
        EventBus.INSTANCE.post(MinecraftInitEvent.INSTANCE);
    }
}
