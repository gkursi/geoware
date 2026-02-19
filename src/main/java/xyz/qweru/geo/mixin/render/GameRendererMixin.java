package xyz.qweru.geo.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.qweru.geo.client.event.GameRenderEvent;
import xyz.qweru.geo.client.event.PostCrosshairEvent;
import xyz.qweru.geo.client.event.PreCrosshairEvent;
import xyz.qweru.geo.client.event.WorldRenderEvent;
import xyz.qweru.geo.client.module.visual.ModuleViewModel;
import xyz.qweru.geo.core.event.EventBus;
import xyz.qweru.geo.core.system.Systems;
import xyz.qweru.geo.core.system.module.Modules;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/debug/DebugScreenEntryList;isCurrentlyEnabled(Lnet/minecraft/resources/Identifier;)Z"))
    private void onWorldRender(DeltaTracker deltaTracker, CallbackInfo ci) {
        EventBus.INSTANCE.post(WorldRenderEvent.INSTANCE);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void render(DeltaTracker deltaTracker, boolean bl, CallbackInfo ci) {
        EventBus.INSTANCE.post(GameRenderEvent.INSTANCE);
    }

    @Unique
    ModuleViewModel viewModel = null;

    @Inject(method = "renderItemInHand", at = @At("HEAD"))
    private void renderHand(float tickProgress, boolean sleeping, Matrix4f positionMatrix, CallbackInfo ci) {
        viewModel = Systems.INSTANCE.get(Modules.class).get(ModuleViewModel.class);
    }

    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void bobView(PoseStack poseStack, float f, CallbackInfo ci) {
        if (viewModel.getEnabled() && !viewModel.getHandSway()) ci.cancel();
    }

    @Inject(method = "pick", at = @At("HEAD"))
    private void preUpdate(float f, CallbackInfo ci) {
        EventBus.INSTANCE.post(PreCrosshairEvent.INSTANCE);
    }

    @Inject(method = "pick", at = @At("RETURN"))
    private void postUpdate(float f, CallbackInfo ci) {
        EventBus.INSTANCE.post(PostCrosshairEvent.INSTANCE);
    }

}
