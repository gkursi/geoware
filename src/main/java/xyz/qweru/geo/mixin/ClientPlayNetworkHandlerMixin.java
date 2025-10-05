package xyz.qweru.geo.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.geo.core.Glob;
import xyz.qweru.geo.core.command.Commands;

import static xyz.qweru.geo.core.Glob.mc;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChat(String content, CallbackInfo ci) {
        if (content.startsWith(Glob.prefix)) {
            try {
                Commands.INSTANCE.execute(content.substring(Glob.prefix.length()));
            } catch (CommandSyntaxException e) {
                mc.player.sendMessage(Text.of("Invalid command: " + e.getMessage()), false);
            }
            ci.cancel();
        }
    }
}
