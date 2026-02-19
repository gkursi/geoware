package xyz.qweru.geo.mixin.render;

import com.mojang.blaze3d.platform.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.geo.core.event.EventBus;
import xyz.qweru.multirender.api.render.event.WindowCloseEvent;

@Mixin(Window.class)
public class WindowMixin {
    @Inject(method = "close", at = @At("HEAD"))
    private void closeWindow(CallbackInfo ci) {
        EventBus.INSTANCE.post(WindowCloseEvent.INSTANCE);
    }
}
