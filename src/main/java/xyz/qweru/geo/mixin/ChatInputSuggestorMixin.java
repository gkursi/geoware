package xyz.qweru.geo.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ClientCommandSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.qweru.geo.core.Glob;
import xyz.qweru.geo.core.command.Command;
import xyz.qweru.geo.core.command.Commands;

import java.util.concurrent.CompletableFuture;

@Mixin(ChatInputSuggestor.class)
public abstract class ChatInputSuggestorMixin {
    @Shadow @Nullable private ParseResults<ClientCommandSource> parse;

    @Shadow @Final
    MinecraftClient client;

    @Shadow @Final
    TextFieldWidget textField;

    @Shadow
    boolean completingSuggestions;

    @Shadow @Nullable private CompletableFuture<Suggestions> pendingSuggestions;

    @Shadow protected abstract void showCommandSuggestions();

    @Inject(method = "refresh", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/StringReader;canRead()Z"), cancellable = true)
    public void refresh(CallbackInfo ci, @Local StringReader reader) {

        String prefix = Glob.prefix;
        int len = prefix.length();

        if (reader.canRead(len) && reader.getString().startsWith(prefix)) {
            reader.setCursor(reader.getCursor() + len);

            if (parse == null) {
                parse = Commands.INSTANCE.getDispatcher().parse(reader, Commands.INSTANCE.getSource());
            }

            int cursor = textField.getCursor();
            if (!completingSuggestions && cursor >= 1) {
                pendingSuggestions = Commands.INSTANCE.getDispatcher().getCompletionSuggestions(parse, cursor);
                pendingSuggestions.thenRun(() -> {
                    if (pendingSuggestions.isDone()) {
                        showCommandSuggestions();
                    }
                });
            }
            ci.cancel();
        }
    }
}
