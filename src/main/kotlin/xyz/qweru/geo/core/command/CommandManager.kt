package xyz.qweru.geo.core.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.ParseResults
import com.mojang.brigadier.exceptions.CommandSyntaxException
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.client.multiplayer.ClientSuggestionProvider
import xyz.qweru.geo.client.command.CommandConfig
import xyz.qweru.geo.client.command.CommandSet
import xyz.qweru.geo.client.command.CommandToggle
import xyz.qweru.geo.core.Core.mc

object CommandManager {
    val dispatcher = CommandDispatcher<ClientSuggestionProvider>()
    val source = ClientSuggestionProvider(mc.connection, mc, false)
    val commands = ObjectArrayList<Command>()

    init {
        add(CommandToggle())
        add(CommandSet())
        add(CommandConfig())
    }

    private fun add(command: Command) {
        commands.add(command)
        command.register()
    }

    @Throws(CommandSyntaxException::class)
    fun execute(line: String?) {
        val results: ParseResults<ClientSuggestionProvider> = dispatcher.parse(line, source)
        dispatcher.execute(results)
    }
}