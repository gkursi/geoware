package xyz.qweru.geo.client.command.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import xyz.qweru.geo.core.config.Config
import xyz.qweru.geo.core.config.Configs
import java.util.concurrent.CompletableFuture

class ConfigArgumentType : ArgumentType<Config> {
    override fun parse(reader: StringReader): Config? {
        val name = reader.readString()
        return Configs.findConfig(name)
    }

    override fun <S : Any> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        Configs.configs.keys.forEach(builder::suggest)
        return builder.buildFuture();
    }
}