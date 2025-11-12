package xyz.qweru.geo.mixin.entity;

import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientboundSetEntityMotionPacket.class)
public interface EntityVelocityUpdateS2CPacketAccessor {
    @Mutable
    @Accessor("xa")
    void geo_setVelocityX(int x);
    @Mutable
    @Accessor("ya")
    void geo_setVelocityY(int x);
    @Mutable
    @Accessor("za")
    void geo_setVelocityZ(int x);
}
