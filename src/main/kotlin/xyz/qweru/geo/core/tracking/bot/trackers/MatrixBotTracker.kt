package xyz.qweru.geo.core.tracking.bot.trackers

import com.mojang.authlib.GameProfile
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.world.entity.player.Player
import xyz.qweru.geo.abstraction.network.ClientConnection
import xyz.qweru.geo.client.event.PacketReceiveEvent
import xyz.qweru.geo.client.event.PreTickEvent
import xyz.qweru.geo.core.Core.mc
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.tracking.PlayerTracker
import xyz.qweru.geo.core.tracking.bot.BotTracker.config
import xyz.qweru.geo.extend.minecraft.entity.armorItems
import xyz.qweru.geo.extend.minecraft.game.theLevel
import xyz.qweru.geo.extend.minecraft.item.isPlayerArmor
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object MatrixBotTracker : PlayerTracker {
    private val trackedBots = ConcurrentHashMap.newKeySet<UUID>()
    private val suspiciousPlayers = ConcurrentHashMap.newKeySet<UUID>()

    private val queued = ConcurrentHashMap.newKeySet<Packet<ClientGamePacketListener>>()

    @Handler
    private fun onPacketReceive(e: PacketReceiveEvent) {
        if (e.packet !is ClientboundPlayerInfoUpdatePacket && e.packet !is ClientboundPlayerInfoRemovePacket) return
        queued.add(e.packet)
    }

    @Handler
    private fun onTick(e: PreTickEvent) {
        if (mc.level == null || suspiciousPlayers.isEmpty()) {
            return
        }

        for (packet in queued) {
            when (packet) {
                is ClientboundPlayerInfoUpdatePacket -> {
                    for (player in packet.entries()) {
                        val profile = player.profile ?: continue
                        if (isUnique(profile)) continue
                        if (profile.properties.isEmpty)
                            suspiciousPlayers.add(player.profileId)
                        if (isDuplicate(profile) || noTag(player.profileId))
                            trackedBots.add(player.profileId)
                    }
                }
                is ClientboundPlayerInfoRemovePacket -> {
                    for (id in packet.profileIds) {
                        trackedBots.remove(id)
                        suspiciousPlayers.remove(id)
                    }
                }
            }
        }

        queued.clear()

        for (player in mc.theLevel.players()) {
            if (!suspiciousPlayers.contains(player.uuid)) {
                continue
            }

            if (isFullyArmored(player)) {
                trackedBots.add(player.uuid)
            }

            suspiciousPlayers.remove(player.uuid)
        }
    }

    private fun noTag(profileId: UUID) =
        mc.connection?.getPlayerInfo(profileId)?.let { noTag(it) } == true

    private fun noTag(entry: PlayerInfo): Boolean
        = config.matrixTag && entry.profile.name.split(" ").size == 1

    override fun isTracking(player: Player): Boolean =
        trackedBots.contains(player.uuid)

    private fun isDuplicate(profile: GameProfile): Boolean {
        return ClientConnection.playerList()?.count { it.profile.name == profile.name && it.profile.id != profile.id } == 1
    }

    private fun isUnique(profile: GameProfile): Boolean {
        return ClientConnection.playerList()?.count { it.profile.name == profile.name && it.profile.id == profile.id } == 1
    }

    private fun isFullyArmored(entity: Player): Boolean {
        return entity.armorItems.all { stack ->
            stack.isPlayerArmor && !stack.enchantments.isEmpty
        }
    }
}