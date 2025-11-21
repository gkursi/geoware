package xyz.qweru.geo.client.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.BuiltInExceptions
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.client.multiplayer.ClientSuggestionProvider
import xyz.qweru.geo.core.command.Command
import xyz.qweru.geo.client.command.argument.ModuleArgumentType
import xyz.qweru.geo.client.command.argument.SettingArgumentType
import xyz.qweru.geo.client.command.argument.SettingValueArgumentType
import xyz.qweru.geo.core.system.module.Module

class CommandSet : Command("set", "Change the value of a setting", "set <module> <setting> <value>") {
    override fun build(builder: LiteralArgumentBuilder<ClientSuggestionProvider>) {
        builder
            .then(RequiredArgumentBuilder.argument<ClientSuggestionProvider, Module>("module", ModuleArgumentType())
                .then(RequiredArgumentBuilder.argument<ClientSuggestionProvider, String>("setting", SettingArgumentType("module"))
                    .then(RequiredArgumentBuilder.argument<ClientSuggestionProvider, String>("value", SettingValueArgumentType("setting", "module")).executes(this::apply))))
    }

    private fun apply(ctx: CommandContext<*>): Int {
        val module = try {
            ModuleArgumentType.get(ctx, "module")
        } catch (_: Throwable) {
            throw CommandSyntaxException(BuiltInExceptions().dispatcherUnknownArgument()) { "Unknown module" }
        }
        val setting = try {
            SettingArgumentType.get(ctx, "setting", module)
        } catch (_: Throwable) {
            throw CommandSyntaxException(BuiltInExceptions().dispatcherUnknownArgument()) { "Unknown module" }
        }
        val value = try {
            SettingValueArgumentType.get(ctx, "value")
        } catch (_: Throwable) {
            throw CommandSyntaxException(BuiltInExceptions().dispatcherUnknownArgument()) { "Unknown value" }
        }

        try {
            setting.parseAndSet(value)
        } catch (_: Throwable) {
            throw CommandSyntaxException(BuiltInExceptions().dispatcherUnknownArgument()) { "Invalid value" }
        }

        return 1
    }
}