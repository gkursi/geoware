package xyz.qweru.geo.client.command.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import xyz.qweru.geo.core.system.config.ConfigType
import java.util.concurrent.CompletableFuture

class ConfigTypeArgumentType : ArgumentType<ConfigType> {
    override fun parse(reader: StringReader): ConfigType? {
        return ConfigType.fromId(reader.readString().lowercase())
    }

    override fun <S : Any?> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        ConfigType.entries.forEach { entry -> builder.suggest(entry.name.lowercase().replaceFirstChar { it.uppercase() }) }
        return builder.buildFuture()
    }
}