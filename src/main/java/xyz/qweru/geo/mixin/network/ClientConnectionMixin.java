package xyz.qweru.geo.mixin.network;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.geo.client.event.PacketEvent;
import xyz.qweru.geo.client.event.PacketQueueEvent;
import xyz.qweru.geo.client.event.PacketReceiveEvent;
import xyz.qweru.geo.client.event.PacketSendEvent;
import xyz.qweru.geo.core.event.EventBus;
import xyz.qweru.geo.imixin.IConnection;
import xyz.qweru.geo.imixin.IPacket;

import java.util.Iterator;

@Mixin(Connection.class)
public abstract class ClientConnectionMixin implements IConnection {

    @Shadow protected abstract void doSendPacket(Packet<?> packet, @Nullable ChannelFutureListener channelFutureListener, boolean bl);

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void onPacket(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof ClientboundBundlePacket p) {
            Iterator<Packet<? super ClientGamePacketListener>> i = p.subPackets().iterator();
            while (i.hasNext()) if (onPacket(i.next(), PacketReceiveEvent.INSTANCE)) i.remove();
            return;
        }
        if (onPacket(packet, PacketReceiveEvent.INSTANCE)) ci.cancel();
    }

    @Inject(method = "doSendPacket", at = @At("HEAD"), cancellable = true)
    private void onPacketSend(Packet<?> packet, ChannelFutureListener channelFutureListener, boolean flush, CallbackInfo ci) {
        if (onPacket(packet, PacketSendEvent.INSTANCE)) {
            ci.cancel();
        }
    }

    @Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true)
    private void onPacketQueue(Packet<?> packet, @Nullable ChannelFutureListener channelFutureListener, boolean bl, CallbackInfo ci) {
        if (onPacket(packet, PacketQueueEvent.INSTANCE)) {
            ci.cancel();
        }
    }

    @Unique
    private boolean onPacket(Packet<?> packet, PacketEvent event) {
        event.setPacket(packet);
        return EventBus.INSTANCE.post(event).getCancelled();
    }

    @Override
    public void geo_doSend(Packet<?> packet) {
        doSendPacket(packet, null, true);
    }
}
