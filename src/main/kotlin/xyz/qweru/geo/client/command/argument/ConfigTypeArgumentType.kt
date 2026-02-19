package xyz.qweru.geo.client.command.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import xyz.qweru.geo.core.config.ConfigType
import java.util.concurrent.CompletableFuture

class ConfigTypeArgumentType : ArgumentType<ConfigType> {
    override fun parse(reader: StringReader): ConfigType {
        val name = reader.readString().lowercase()
        return ConfigType.fromId(name) ?: throw IllegalArgumentException("$name is not a config type (possible types: ${ConfigType.entries.joinToString()}")
    }

    override fun <S : Any?> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        ConfigType.entries.forEach { entry -> builder.suggest(entry.id) }
        return builder.buildFuture()
    }
}