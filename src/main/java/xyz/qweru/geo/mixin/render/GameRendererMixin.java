package xyz.qweru.geo.mixin.render;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.qweru.geo.client.event.GameRenderEvent;
import xyz.qweru.geo.client.event.PostCrosshair;
import xyz.qweru.geo.client.event.PreCrosshair;
import xyz.qweru.geo.client.event.WorldRenderEvent;
import xyz.qweru.geo.client.module.visual.ModuleViewModel;
import xyz.qweru.geo.core.event.EventBus;
import xyz.qweru.geo.core.system.Systems;
import xyz.qweru.geo.core.system.module.Modules;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;shouldRenderCrosshair()Z"))
    private void onWorldRender(RenderTickCounter renderTickCounter, CallbackInfo ci) {
        EventBus.INSTANCE.post(WorldRenderEvent.INSTANCE);
    }

    @Unique
    ModuleViewModel viewModel = null;

    @Inject(method = "renderHand", at = @At("HEAD"))
    private void renderHand(float tickProgress, boolean sleeping, Matrix4f positionMatrix, CallbackInfo ci) {
        viewModel = Systems.INSTANCE.get(Modules.class).get(ModuleViewModel.class);
    }

    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void bobView(MatrixStack matrices, float tickProgress, CallbackInfo ci) {
        if (viewModel.getEnabled() && !viewModel.getHandSway()) ci.cancel();
    }

    @Inject(method = "findCrosshairTarget", at = @At("HEAD"))
    private void preUpdate(Entity camera, double blockInteractionRange, double entityInteractionRange, float tickProgress, CallbackInfoReturnable<HitResult> cir) {
        EventBus.INSTANCE.post(PreCrosshair.INSTANCE);
    }

    @Inject(method = "findCrosshairTarget", at = @At("RETURN"))
    private void postUpdate(Entity camera, double blockInteractionRange, double entityInteractionRange, float tickProgress, CallbackInfoReturnable<HitResult> cir) {
        EventBus.INSTANCE.post(PostCrosshair.INSTANCE);
    }

}
