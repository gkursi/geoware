package xyz.qweru.geo.mixin.render;

import net.minecraft.client.util.Window;
import net.minecraft.client.util.tracy.TracyFrameCapturer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.geo.client.event.FramebufferSizeChangeEvent;
import xyz.qweru.geo.client.event.SwapBufferEvent;
import xyz.qweru.geo.core.Global;
import xyz.qweru.geo.core.event.EventBus;

@Mixin(Window.class)
public class WindowMixin {
    @Inject(method = "onFramebufferSizeChanged", at = @At("TAIL"))
    private void onSize(long window, int width, int height, CallbackInfo ci) {
        FramebufferSizeChangeEvent.INSTANCE.setWidth(width);
        FramebufferSizeChangeEvent.INSTANCE.setHeight(height);
        EventBus.INSTANCE.post(FramebufferSizeChangeEvent.INSTANCE);
    }

    @Inject(method = "swapBuffers", at = @At("HEAD"))
    private void onSwapBuffers(TracyFrameCapturer capturer, CallbackInfo ci) {
        EventBus.INSTANCE.post(SwapBufferEvent.INSTANCE);
    }
}
