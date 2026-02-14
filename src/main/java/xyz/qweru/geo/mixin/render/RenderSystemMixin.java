package xyz.qweru.geo.mixin.render;

import com.mojang.blaze3d.shaders.ShaderSource;
import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.geo.client.event.PostRenderInitEvent;
import xyz.qweru.geo.core.event.EventBus;

import java.util.function.BiFunction;

@Mixin(RenderSystem.class)
public class RenderSystemMixin {
    @Inject(method = "initRenderer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/DynamicUniforms;<init>()V", shift = At.Shift.AFTER))
    private static void postInit(long l, int i, boolean bl, ShaderSource shaderSource, boolean bl2, CallbackInfo ci) {
        EventBus.INSTANCE.post(PostRenderInitEvent.INSTANCE);
    }
}
