package xyz.qweru.geo.client.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.client.network.ClientCommandSource
import xyz.qweru.geo.core.command.Command

class CommandHelp : Command("help", "Displays a help message", "help") {
    override fun build(builder: LiteralArgumentBuilder<ClientCommandSource>) {}
}