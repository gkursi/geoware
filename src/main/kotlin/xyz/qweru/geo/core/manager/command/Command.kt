package xyz.qweru.geo.core.manager.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.client.multiplayer.ClientSuggestionProvider

abstract class Command(val name: String, val description: String, vararg val usage: String) {
    fun register() {
        val builder = LiteralArgumentBuilder.literal<ClientSuggestionProvider>(name)
        build(builder)
        CommandManager.dispatcher.register(builder)
    }

    protected abstract fun build(builder: LiteralArgumentBuilder<ClientSuggestionProvider>)
}