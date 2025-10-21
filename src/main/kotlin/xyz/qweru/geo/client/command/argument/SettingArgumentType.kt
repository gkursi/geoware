package xyz.qweru.geo.client.command.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.core.system.setting.Setting
import java.util.concurrent.CompletableFuture

class SettingArgumentType(val moduleArgName: String) : ArgumentType<String> {

    companion object {
        fun get(ctx: CommandContext<*>, setting: String, module: Module): Setting<*, *> {
            val name = ctx.getArgument(setting, String::class.java).lowercase()
            return module.settings.allSettings.stream().filter { it.name.lowercase() == name }.findFirst().orElseThrow()
        }
    }

    override fun parse(reader: StringReader): String = reader.readUnquotedString()

    override fun <S : Any?> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        val input = builder.remaining.lowercase()
        val module = try {
            ModuleArgumentType.get(context, moduleArgName)
        } catch (_: Throwable) {
            return builder.buildFuture()
        }
        for (setting in module.settings.allSettings) {
            if (!setting.name.lowercase().startsWith(input)) continue
            builder.suggest(setting.name)
        }
        return builder.buildFuture()
    }
}
