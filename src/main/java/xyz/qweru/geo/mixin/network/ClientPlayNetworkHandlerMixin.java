package xyz.qweru.geo.mixin.network;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.geo.core.Global;
import xyz.qweru.geo.core.manager.command.CommandManager;

import static xyz.qweru.geo.core.Global.mc;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChat(String content, CallbackInfo ci) {
        if (content.startsWith(Global.PREFIX)) {
            try {
                CommandManager.INSTANCE.execute(content.substring(Global.PREFIX.length()));
            } catch (CommandSyntaxException e) {
                mc.player.sendMessage(Text.of("Invalid command: " + e.getMessage()), false);
            }
            ci.cancel();
        }
    }
}
