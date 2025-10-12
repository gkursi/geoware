package xyz.qweru.geo.client.command

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.exceptions.BuiltInExceptions
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.client.network.ClientCommandSource
import xyz.qweru.geo.core.command.Command
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.core.system.module.Modules
import xyz.qweru.geo.core.system.Systems
import java.util.concurrent.CompletableFuture

class CommandToggle : Command("toggle", "Toggle a module", "toggle <module>") {
    override fun build(builder: LiteralArgumentBuilder<ClientCommandSource>) {
        builder.then(RequiredArgumentBuilder.argument<ClientCommandSource, String>("module", StringArgumentType.word()).suggests { ctx, build ->
            for (system in Systems.get(Modules::class).getSubsystems())
                build.suggest(system.name)
            return@suggests CompletableFuture.completedFuture(build.build())
        }.executes { ctx ->
            try {
                val ms = StringArgumentType.getString(ctx, "module")
                val module = Systems.get(Modules::class).getSubsystems().find { s -> s.name == ms } as Module
                module.enabled = !module.enabled
            } catch (e: Throwable) {
                throw CommandSyntaxException(BuiltInExceptions().dispatcherUnknownArgument()) {"Unknown module"}
            }
            return@executes 1
        })
    }
}