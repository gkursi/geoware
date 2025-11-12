package xyz.qweru.geo.mixin.game;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.geo.core.Global;
import xyz.qweru.geo.core.manager.command.CommandManager;

import java.util.concurrent.CompletableFuture;

@Mixin(CommandSuggestions.class)
public abstract class ChatInputSuggestorMixin {

    @Shadow @Nullable private CompletableFuture<Suggestions> pendingSuggestions;

    @Shadow @Nullable private ParseResults<ClientSuggestionProvider> currentParse;

    @Shadow @Final private EditBox input;

    @Shadow public abstract void showSuggestions(boolean bl);

    @Shadow protected abstract void updateUsageInfo();

    @Inject(method = "updateCommandInfo", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/StringReader;canRead()Z"), cancellable = true)
    public void refresh(CallbackInfo ci, @Local StringReader reader) {

        String prefix = Global.PREFIX;
        int len = prefix.length();

        if (reader.canRead(len) && reader.getString().startsWith(prefix)) {
            reader.setCursor(reader.getCursor() + len);

            if (currentParse == null) {
                currentParse = CommandManager.INSTANCE.getDispatcher().parse(reader, CommandManager.INSTANCE.getSource());
            }

            int cursor = input.getCursorPosition();
            if (pendingSuggestions == null && cursor >= 1) {
                pendingSuggestions = CommandManager.INSTANCE.getDispatcher().getCompletionSuggestions(currentParse, cursor);
                pendingSuggestions.thenRun(() -> {
                    if (pendingSuggestions.isDone()) {
                        updateUsageInfo();
                    }
                });
            }
            ci.cancel();
        }
    }
}
