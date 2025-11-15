package xyz.qweru.geo.client.command

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.client.multiplayer.ClientSuggestionProvider
import xyz.qweru.geo.client.command.argument.ConfigArgumentType
import xyz.qweru.geo.client.command.argument.ConfigTypeArgumentType
import xyz.qweru.geo.client.command.argument.NewConfigArgumentType
import xyz.qweru.geo.core.Global
import xyz.qweru.geo.core.manager.command.Command
import xyz.qweru.geo.core.system.SystemCache
import xyz.qweru.geo.core.system.Systems
import xyz.qweru.geo.core.system.config.Config
import xyz.qweru.geo.core.system.config.ConfigType
import xyz.qweru.geo.core.system.config.Configs

class CommandConfig : Command("config", "Save/load/export configs",
    "config <save|load> <config name>", "config export <config name> <export type>") {

    companion object {
        val configs: Configs by SystemCache.get()
    }

    override fun build(builder: LiteralArgumentBuilder<ClientSuggestionProvider>) {
        builder.then(
            RequiredArgumentBuilder.argument<ClientSuggestionProvider, String>("action", StringArgumentType.word())
                .suggests { _, builder ->
                    builder.suggest("save")
                    builder.suggest("load")
                    builder.buildFuture()
                }
                .then(
                    RequiredArgumentBuilder.argument<ClientSuggestionProvider, Config>("config", ConfigArgumentType())
                        .executes(this::saveOrLoad)
                )
        ).then(
            LiteralArgumentBuilder.literal<ClientSuggestionProvider>("export")
                .then(
                    RequiredArgumentBuilder.argument<ClientSuggestionProvider, String>("config", NewConfigArgumentType())
                        .then(
                            RequiredArgumentBuilder.argument<ClientSuggestionProvider, ConfigType>("type", ConfigTypeArgumentType())
                                .executes(this::export)
                        )

                )
        )
    }

    private fun saveOrLoad(ctx: CommandContext<ClientSuggestionProvider>): Int {
        try {
            val action = StringArgumentType.getString(ctx, "action")
            val config = ctx.getArgument("config", Config::class.java)

            when (action) {
                "save" -> {
                    configs.save(config.name, config.type)
                    Global.logger.info("saved ${config.name}")
                }
                "load" -> configs.loadConfig(config)
                else -> throw IllegalArgumentException("Invalid action")
            }

            return 1
        } catch (e: NullPointerException) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create().also {
                it.addSuppressed(e)
            }
        }
    }

    private fun export(ctx: CommandContext<ClientSuggestionProvider>): Int {
        val config = StringArgumentType.getString(ctx, "config")
        val type = ctx.getArgument("type", ConfigType::class.java)
        Systems.get(Configs::class).save(config, type)
        return 1
    }
}