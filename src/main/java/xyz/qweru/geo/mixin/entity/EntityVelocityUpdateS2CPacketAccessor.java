package xyz.qweru.geo.mixin.entity;

import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityVelocityUpdateS2CPacket.class)
public interface EntityVelocityUpdateS2CPacketAccessor {
    @Mutable
    @Accessor("velocityX")
    void geo_setVelocityX(int x);
    @Mutable
    @Accessor("velocityY")
    void geo_setVelocityY(int x);
    @Mutable
    @Accessor("velocityZ")
    void geo_setVelocityZ(int x);
}
