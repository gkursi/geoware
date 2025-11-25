package xyz.qweru.geo.mixin.game;

import net.minecraft.client.User;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.UUID;

@Mixin(User.class)
public class SessionMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(String string, UUID uUID, String string2, Optional optional, Optional optional2, User.Type type, CallbackInfo ci) {

    }

}
