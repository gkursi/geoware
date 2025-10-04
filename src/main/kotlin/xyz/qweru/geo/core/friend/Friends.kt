package xyz.qweru.geo.core.friend

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import net.minecraft.entity.player.PlayerEntity
import xyz.qweru.geo.core.system.System
import java.util.*

class Friends : System("friends") {
    private val friends = ObjectOpenHashSet<UUID>()

    fun add(uuid: UUID) = friends.add(uuid)
    fun isFriend(uuid: UUID) = friends.contains(uuid)
    fun isFriend(playerEntity: PlayerEntity) = friends.contains(playerEntity.gameProfile.id)

    override fun initThis() {}

    override fun loadThis(json: JsonObject) {
        val friends = json["friends"]
        if (friends != null) for (element in friends.asJsonArray) {
            val content = element.asString
            try {
                this.friends.add(UUID.fromString(content))
            } catch (_: IllegalArgumentException) {
                logger.warn("Skipping invalid friend UUID: $content")
            }
        }
    }

    override fun saveThis(json: JsonObject) {
        val array = JsonArray(friends.size)
        for (id in friends)
            array.add(JsonPrimitive(id.toString()))
        json.add("friends", array)
    }
}