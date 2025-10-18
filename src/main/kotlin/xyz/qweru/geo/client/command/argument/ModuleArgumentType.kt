package xyz.qweru.geo.client.command.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import xyz.qweru.geo.core.system.Systems
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.core.system.module.Modules
import java.util.concurrent.CompletableFuture

class ModuleArgumentType : ArgumentType<Module> {

    companion object {
        fun get(ctx: CommandContext<*>, name: String): Module =
            ctx.getArgument(name, Module::class.java)
    }

    override fun parse(reader: StringReader): Module? {
        val name = reader.readUnquotedString()
        return Systems.get(Modules::class).get(name)
    }

    override fun <S> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        val input = builder.remaining.lowercase()
        for (system in Systems.get(Modules::class).getSubsystems())
            if (system.name.lowercase().startsWith(input)) builder.suggest(system.name)
        return builder.buildFuture()
    }
}