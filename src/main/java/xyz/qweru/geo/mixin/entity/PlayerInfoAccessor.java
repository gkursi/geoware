package xyz.qweru.geo.mixin.entity;

import net.minecraft.client.multiplayer.PlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerInfo.class)
public interface PlayerInfoAccessor {

    @Accessor("latency")
    int geo_getLatency();

}
