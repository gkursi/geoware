package xyz.qweru.geo.mixin;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.item.Items;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.geo.helper.player.HotbarHelper;

import static xyz.qweru.geo.core.Glob.mc;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"))
    private void onPacket(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof EntityStatusS2CPacket p && p.getStatus() == 35
                && p.getEntity(mc.world) == mc.player) {
            // TODO reimplement
//            if (Config.INSTANCE.getAUTO_DOUBLE_HAND()) sm_doubleHand();
//            if (Config.INSTANCE.getAUTO_TOTEM()) sm_totem();
        }
    }

    @Unique
    private void sm_doubleHand() {
        HotbarHelper.INSTANCE.swap(i -> i.isOf(Items.TOTEM_OF_UNDYING));
        // TODO reimplement
//        if (Config.SCROLL_SWAP) Temp.INSTANCE.setFinishTotemSwap(HotbarHelper.INSTANCE.isInMainhand(it -> it.isOf(Items.TOTEM_OF_UNDYING)));
    }

    @Unique
    private void sm_totem() {
        // TODO reimplement
//        if (Config.INSTANCE.getOPEN_INV() && mc.currentScreen == null
////                && !HotbarHelper.INSTANCE.find(s -> s.isOf(Items.TOTEM_OF_UNDYING)).found()
//        ) {
//            API.keyboardHandler.press(GLFW.GLFW_KEY_E);
//            API.keyboardHandler.release(GLFW.GLFW_KEY_E);
//        }
    }

}
