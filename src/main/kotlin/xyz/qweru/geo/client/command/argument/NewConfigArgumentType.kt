package xyz.qweru.geo.client.command.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.concurrent.CompletableFuture

class NewConfigArgumentType : ArgumentType<String> {
    override fun parse(reader: StringReader): String = reader.readString()

    override fun <S : Any> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        builder.suggest("my-config-1")
        builder.suggest("example-name-3")
        builder.suggest("insane-visuals")
        builder.suggest("hypixel-bypass")
        return builder.buildFuture();
    }
}