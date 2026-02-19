package xyz.qweru.geo.mixin.game;

import net.minecraft.client.User;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.geo.imixin.IUser;

import java.util.Optional;
import java.util.UUID;

@Mixin(User.class)
public class UserMixin implements IUser {

    @Mutable
    @Shadow
    @Final
    private String accessToken;

    @Mutable
    @Shadow
    @Final
    private String name;

    @Mutable
    @Shadow
    @Final
    private UUID uuid;

    @Mutable
    @Shadow
    @Final
    private Optional<String> clientId;

    @Mutable
    @Shadow
    @Final
    private Optional<String> xuid;

    @Override
    public void geo_setToken(String session) {
        this.accessToken = session;
    }

    @Override
    public void geo_setUsername(String username) {
        this.name = username;
    }

    @Override
    public void geo_setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void geo_setClientId(Optional<String> id) {
        this.clientId = id;
    }

    @Override
    public void geo_setXid(Optional<String> id) {
        this.xuid = id;
    }
}
