package xyz.qweru.geo.mixin.game;

import com.mojang.authlib.GameProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Mixin(GameProfile.class)
public interface GameProfileAccessor {

    @Mutable
    @Accessor("id")
    void geo_setId(UUID id);

}
