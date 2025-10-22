package xyz.qweru.geo.client.command

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.client.network.ClientCommandSource
import xyz.qweru.geo.client.command.argument.ConfigArgumentType
import xyz.qweru.geo.client.command.argument.ConfigTypeArgumentType
import xyz.qweru.geo.client.command.argument.NewConfigArgumentType
import xyz.qweru.geo.core.Global
import xyz.qweru.geo.core.manager.command.Command
import xyz.qweru.geo.core.system.Systems
import xyz.qweru.geo.core.system.config.Config
import xyz.qweru.geo.core.system.config.ConfigType
import xyz.qweru.geo.core.system.config.Configs

class CommandConfig : Command("config", "Save/load/export configs",
    "config <save|load> <config name>", "config export <config name> <export type>") {
    override fun build(builder: LiteralArgumentBuilder<ClientCommandSource>) {
        builder.then(
            RequiredArgumentBuilder.argument<ClientCommandSource, String>("action", StringArgumentType.word())
                .suggests { _, builder ->
                    builder.suggest("save")
                    builder.suggest("load")
                    builder.buildFuture()
                }
                .then(
                    RequiredArgumentBuilder.argument<ClientCommandSource, Config>("config", ConfigArgumentType())
                        .executes(this::saveOrLoad)
                )
        ).then(
            LiteralArgumentBuilder.literal<ClientCommandSource>("export")
                .then(
                    RequiredArgumentBuilder.argument<ClientCommandSource, String>("config", NewConfigArgumentType())
                        .then(
                            RequiredArgumentBuilder.argument<ClientCommandSource, ConfigType>("type", ConfigTypeArgumentType())
                                .executes(this::export)
                        )

                )
        )
    }

    private fun saveOrLoad(ctx: CommandContext<ClientCommandSource>): Int {
        try {
            val action = StringArgumentType.getString(ctx, "action")
            val config = ctx.getArgument("config", Config::class.java)
            val configs = Systems.get(Configs::class)

            when (action) {
                "save" -> {
                    configs.writeConfig(config)
                    configs.saveConfig(config)
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

    private fun export(ctx: CommandContext<ClientCommandSource>): Int {
        val config = StringArgumentType.getString(ctx, "config")
        val type = ctx.getArgument("type", ConfigType::class.java)
        Systems.get(Configs::class).save(config, type)
        return 1
    }
}