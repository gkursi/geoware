package xyz.qweru.geo.mixin.network.packet;

import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.world.entity.player.Input;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerboundPlayerInputPacket.class)
public interface ServerboundPlayerInputPacketAccessor {
    @Mutable
    @Accessor("input")
    void geo_setInput(Input input);
}
