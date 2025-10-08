package xyz.qweru.geo.mixin.network;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.item.Items;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.geo.client.event.PacketEvent;
import xyz.qweru.geo.client.event.PacketReceiveEvent;
import xyz.qweru.geo.client.event.PacketSendEvent;
import xyz.qweru.geo.core.event.Events;
import xyz.qweru.geo.helper.player.HotbarHelper;

import static xyz.qweru.geo.core.Glob.mc;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"))
    private void onPacket(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof BundleS2CPacket p) {
            p.getPackets().forEach(pck -> onPacket(pck, PacketReceiveEvent.INSTANCE));
            return;
        }
        onPacket(packet, PacketReceiveEvent.INSTANCE);
    }

    @Inject(method = "sendInternal", at = @At("HEAD"))
    private void onPacketSend(Packet<?> packet, ChannelFutureListener channelFutureListener, boolean flush, CallbackInfo ci) {
        onPacket(packet, PacketSendEvent.INSTANCE);
    }

    @Unique
    private void onPacket(Packet<?> packet, PacketEvent event) {
        event.setPacket(packet);
        Events.INSTANCE.post(event);
    }

}
