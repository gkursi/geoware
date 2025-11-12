package xyz.qweru.geo.mixin.render;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.geo.client.event.VanillaHudRenderEvent;
import xyz.qweru.geo.core.event.EventBus;

@Mixin(Gui.class)
public class InGameHudMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void renderHUD(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        EventBus.INSTANCE.post(VanillaHudRenderEvent.INSTANCE);
    }
}
