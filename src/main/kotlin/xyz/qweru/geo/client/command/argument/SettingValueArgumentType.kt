package xyz.qweru.geo.client.command.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import xyz.qweru.geo.extend.kotlin.string.readAllRemaining
import java.util.concurrent.CompletableFuture

class SettingValueArgumentType(val settingArgName: String, val moduleArgName: String) : ArgumentType<String> {

    companion object {
        fun get(ctx: CommandContext<*>, name: String): String =
            ctx.getArgument(name, String::class.java)
    }

    override fun parse(reader: StringReader): String = reader.readAllRemaining()

    override fun <S> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        val module = ModuleArgumentType.get(context, moduleArgName)
        try {
            val setting = SettingArgumentType.get(context, settingArgName, module)
            return setting.suggest<S>(builder)
        } catch (_: Throwable) {
            return builder.buildFuture()
        }
    }
}