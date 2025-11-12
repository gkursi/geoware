package xyz.qweru.geo.mixin.render;

import com.mojang.blaze3d.TracyFrameCapture;
import com.mojang.blaze3d.platform.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.geo.client.event.FramebufferSizeChangeEvent;
import xyz.qweru.geo.client.event.SwapBufferEvent;
import xyz.qweru.geo.core.event.EventBus;

@Mixin(Window.class)
public class WindowMixin {
    @Inject(method = "onFramebufferResize", at = @At("TAIL"))
    private void onSize(long window, int width, int height, CallbackInfo ci) {
        FramebufferSizeChangeEvent.INSTANCE.setWidth(width);
        FramebufferSizeChangeEvent.INSTANCE.setHeight(height);
        EventBus.INSTANCE.post(FramebufferSizeChangeEvent.INSTANCE);
    }

    @Inject(method = "updateDisplay", at = @At("HEAD"))
    private void onSwapBuffers(TracyFrameCapture tracyFrameCapture, CallbackInfo ci) {
        EventBus.INSTANCE.post(SwapBufferEvent.INSTANCE);
    }
}
