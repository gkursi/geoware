package xyz.qweru.geo.mixin.game;

import net.minecraft.client.User;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.UUID;

@Mixin(User.class)
public class SessionMixin {

    @Mutable
    @Shadow @Final private String name;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(String string, UUID uUID, String string2, Optional optional, Optional optional2, User.Type type, CallbackInfo ci) {
//        if (accountType != Session.AccountType.MSA)
//            this.name = "SM111811274";
    }

}
