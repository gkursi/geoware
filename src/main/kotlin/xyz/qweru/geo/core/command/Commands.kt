package xyz.qweru.geo.core.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.ParseResults
import com.mojang.brigadier.exceptions.CommandSyntaxException
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.client.network.ClientCommandSource
import net.minecraft.command.CommandSource
import xyz.qweru.geo.client.command.CommandSet
import xyz.qweru.geo.client.command.CommandToggle
import xyz.qweru.geo.core.Glob.mc
import kotlin.jvm.Throws


object Commands {
    val dispatcher = CommandDispatcher<ClientCommandSource>()
    val source = ClientCommandSource(mc.networkHandler, mc, false)
    val commands = ObjectArrayList<Command>()

    fun register() {
        add(CommandToggle())
        add(CommandSet())
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