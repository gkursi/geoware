package xyz.qweru.geo.mixin;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.geo.client.event.GameRenderEvent;
import xyz.qweru.geo.client.event.WorldRenderEvent;
import xyz.qweru.geo.core.event.Events;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void onFrame(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        Events.INSTANCE.post(GameRenderEvent.INSTANCE);
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;shouldRenderCrosshair()Z"))
    private void onWorldRender(RenderTickCounter renderTickCounter, CallbackInfo ci) {
        Events.INSTANCE.post(WorldRenderEvent.INSTANCE);
    }

}
