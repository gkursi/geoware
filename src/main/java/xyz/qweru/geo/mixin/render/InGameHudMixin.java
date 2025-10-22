package xyz.qweru.geo.mixin.render;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.geo.client.event.HudRenderEvent;
import xyz.qweru.geo.core.event.EventBus;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void renderHUD(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        EventBus.INSTANCE.post(HudRenderEvent.INSTANCE);
    }
}
