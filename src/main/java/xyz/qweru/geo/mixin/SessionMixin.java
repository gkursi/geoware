package xyz.qweru.geo.mixin;

import net.minecraft.client.session.Session;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.UUID;

@Mixin(Session.class)
public class SessionMixin {

    @Mutable
    @Shadow @Final private String username;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(String username, UUID uuid, String accessToken, Optional xuid, Optional clientId, Session.AccountType accountType, CallbackInfo ci) {
        if (accountType != Session.AccountType.MSA) this.username = "SM111811274";
    }

}
