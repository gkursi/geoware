package xyz.qweru.geo.core.manager.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.ParseResults
import com.mojang.brigadier.exceptions.CommandSyntaxException
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.client.network.ClientCommandSource
import xyz.qweru.geo.client.command.CommandConfig
import xyz.qweru.geo.client.command.CommandSet
import xyz.qweru.geo.client.command.CommandToggle
import xyz.qweru.geo.core.Global.mc
import kotlin.jvm.Throws


object CommandManager {
    val dispatcher = CommandDispatcher<ClientCommandSource>()
    val source = ClientCommandSource(mc.networkHandler, mc, false)
    val commands = ObjectArrayList<Command>()

    fun register() {
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
        val results: ParseResults<ClientCommandSource> = dispatcher.parse(line, source)
        dispatcher.execute(results)
    }
}