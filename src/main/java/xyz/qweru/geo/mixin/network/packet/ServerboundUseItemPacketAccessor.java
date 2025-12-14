package xyz.qweru.geo.mixin.network.packet;

import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerboundUseItemPacket.class)
public interface ServerboundUseItemPacketAccessor {

    @Accessor("xRot")
    void geo_setXRot(float xRot);

    @Accessor("yRot")
    void geo_setYRot(float yRot);

}
