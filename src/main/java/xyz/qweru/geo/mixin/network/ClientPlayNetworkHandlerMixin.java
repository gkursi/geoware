package xyz.qweru.geo.mixin.network;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.geo.core.Core;
import xyz.qweru.geo.core.command.CommandManager;

import static xyz.qweru.geo.core.Core.mc;

@Mixin(ClientPacketListener.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "sendChat", at = @At("HEAD"), cancellable = true)
    private void onSendChat(String content, CallbackInfo ci) {
        if (content.startsWith(Core.PREFIX)) {
            try {
                CommandManager.INSTANCE.execute(content.substring(Core.PREFIX.length()));
            } catch (CommandSyntaxException e) {
                mc.player.displayClientMessage(Component.literal("Invalid command: " + e.getMessage()), false);
            }
            ci.cancel();
        }
    }
}
