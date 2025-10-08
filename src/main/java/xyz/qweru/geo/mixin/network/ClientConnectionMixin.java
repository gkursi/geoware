package xyz.qweru.geo.mixin.network;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.geo.client.event.PacketEvent;
import xyz.qweru.geo.client.event.PacketReceiveEvent;
import xyz.qweru.geo.client.event.PacketSendEvent;
import xyz.qweru.geo.core.event.Events;

import java.util.Iterator;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void onPacket(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof BundleS2CPacket p) {
            Iterator<Packet<? super ClientPlayPacketListener>> i = p.getPackets().iterator();
            while (i.hasNext()) if (onPacket(i.next(), PacketReceiveEvent.INSTANCE)) i.remove();
            return;
        }
        if (onPacket(packet, PacketReceiveEvent.INSTANCE)) ci.cancel();
    }

    @Inject(method = "sendInternal", at = @At("HEAD"), cancellable = true)
    private void onPacketSend(Packet<?> packet, ChannelFutureListener channelFutureListener, boolean flush, CallbackInfo ci) {
        if (onPacket(packet, PacketSendEvent.INSTANCE)) {
            ci.cancel();
        }
    }

    @Unique
    private boolean onPacket(Packet<?> packet, PacketEvent event) {
        event.setPacket(packet);
        return Events.INSTANCE.post(event).getCancelled();
    }

}
