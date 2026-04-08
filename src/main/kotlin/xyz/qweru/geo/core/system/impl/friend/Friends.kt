package xyz.qweru.geo.core.system.impl.friend

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import net.minecraft.world.entity.player.Player
import xyz.qweru.geo.core.system.System
import java.util.*

class Friends : System("friends", Type.ROOT) {
    private val friends = ObjectOpenHashSet<UUID>()

    fun add(uuid: UUID) = friends.add(uuid)
    fun isFriend(uuid: UUID) = friends.contains(uuid)
    fun isFriend(player: Player) = isFriend(player.gameProfile.id)

    override fun initThis() {}

    override fun loadThis(json: JsonObject) {
        val friends = json["list"]
        this.friends.clear()
        if (friends != null) for (element in friends.asJsonArray) {
            val content = element.asString
            try {
                this.friends.add(UUID.fromString(content))
            } catch (_: IllegalArgumentException) {
                logger.warn("Skipping invalid friend UUID: $content")
            }
        }
        this.friends.trim()
    }

    override fun saveThis(json: JsonObject) {
        val array = JsonArray(friends.size)
        for (id in friends)
            array.add(JsonPrimitive(id.toString()))
        json.add("list", array)
    }
}