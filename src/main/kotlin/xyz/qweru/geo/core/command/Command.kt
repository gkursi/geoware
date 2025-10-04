package xyz.qweru.geo.core.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.client.network.ClientCommandSource
import net.minecraft.client.network.ClientPlayerEntity

abstract class Command(val name: String, val description: String, vararg val usage: String) {
    fun register() {
        val builder = LiteralArgumentBuilder.literal<ClientCommandSource>(name)
        build(builder)
        Commands.dispatcher.register(builder)
    }

    protected abstract fun build(builder: LiteralArgumentBuilder<ClientCommandSource>)
}