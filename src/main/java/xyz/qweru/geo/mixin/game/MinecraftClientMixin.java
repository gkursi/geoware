package xyz.qweru.geo.mixin.game;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.geo.client.event.PostTickEvent;
import xyz.qweru.geo.client.event.PreTickEvent;
import xyz.qweru.geo.core.event.Events;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow @Nullable public ClientPlayerEntity player;

    @Shadow @Nullable public ClientWorld world;

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void onTick(CallbackInfo ci) {
        Events.INSTANCE.post(PreTickEvent.INSTANCE);
    }

    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    private void postTick(CallbackInfo ci) {
        Events.INSTANCE.post(PostTickEvent.INSTANCE);
    }

}
