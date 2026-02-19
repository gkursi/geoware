package xyz.qweru.geo.client.command

import com.google.gson.JsonParser
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.util.UndashedUuid
import net.minecraft.client.multiplayer.ClientSuggestionProvider
import xyz.qweru.geo.core.Core.mc
import xyz.qweru.geo.core.command.Command
import xyz.qweru.geo.core.ui.notification.Notifications
import xyz.qweru.geo.imixin.IUser
import xyz.qweru.geo.mixin.game.GameProfileAccessor
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URI
import java.util.Optional

class CommandSession : Command("session", "Load a session token from the clipboard", "session") {
    override fun build(builder: LiteralArgumentBuilder<ClientSuggestionProvider>) {
        builder.executes { _ ->
                val session = mc.keyboardHandler.clipboard

                try {
                    val c = URI("https://api.minecraftservices.com/minecraft/profile/")
                        .toURL()
                        .openConnection()
                    c.setRequestProperty("Content-type", "application/json")
                    c.setRequestProperty("Authorization", "Bearer $session")
                    c.setDoOutput(true)

                    val json = JsonParser.parseReader(
                        BufferedReader(InputStreamReader(c.getInputStream()))
                    ).asJsonObject

                    val username = json["name"].asString
                    val id = json["id"].asString
                    val uuid = UndashedUuid.fromString(id)

                    val user = mc.user as IUser
                    user.geo_setToken(session)
                    user.geo_setUsername(username)
                    user.geo_setUuid(uuid)
                    user.geo_setClientId(Optional.empty())
                    user.geo_setXid(Optional.empty())

                    (mc.player?.gameProfile as GameProfileAccessor).geo_setId(uuid)

                    Notifications.info("Set session to $username:$id")
                } catch (e: Exception) {
                    Notifications.error("Failed to set session: ${e.message}")
                }

                return@executes 1
            }
    }
}