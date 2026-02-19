package xyz.qweru.geo.client.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.exceptions.BuiltInExceptions
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.client.multiplayer.ClientSuggestionProvider
import xyz.qweru.geo.client.command.argument.ModuleArgumentType
import xyz.qweru.geo.core.command.Command
import xyz.qweru.geo.core.system.impl.module.Module

class CommandToggle : Command("toggle", "Toggle a module", "toggle <module>") {
    override fun build(builder: LiteralArgumentBuilder<ClientSuggestionProvider>) {
        builder.then(RequiredArgumentBuilder.argument<ClientSuggestionProvider, Module>("module", ModuleArgumentType())
            .executes { ctx ->
                val module = try {
                    ModuleArgumentType.get(ctx, "module")
                } catch (_: Throwable) {
                    throw CommandSyntaxException(BuiltInExceptions().dispatcherUnknownArgument()) { "Unknown module" }
                }
                module.enabled = !module.enabled
                return@executes 1
            })
    }
}