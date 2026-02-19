package xyz.qweru.geo.client.module.move

import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ServerPacketListener
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket
import net.minecraft.network.protocol.game.ServerboundUseItemPacket
import net.minecraft.world.phys.Vec3
import xyz.qweru.geo.client.event.PacketSendEvent
import xyz.qweru.geo.client.helper.network.PacketHelper
import xyz.qweru.geo.core.event.Handler
import xyz.qweru.geo.core.system.module.Category
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.minecraft.entity.pos
import xyz.qweru.geo.extend.minecraft.game.thePlayer

class ModuleFastProjectile : Module("FastProjectiles","guh", Category.MOVEMENT) {
    val sg = settings.general
    val count by sg.int("Count", "Move packet count", 2, 1, 20)
    val len by sg.float("Length", "Move packet size", 1f, 1f, 20f)

    @Handler
    private fun onPacket(event: PacketSendEvent) {
        val packet = event.packet
        if (!validPacket(packet) || !inGame) return

        val vec = mc.thePlayer.pos
        val angle = mc.thePlayer.lookAngle
        val move = Vec3(angle.x, 0.0, angle.z).normalize()

        val offset = len.toDouble()
        for (i in 0..count) {
            PacketHelper.sendPacket(ServerboundMovePlayerPacket.Pos(vec.subtract(move.scale(offset * (i + 1))), false, false))
        }
        for (i in count downTo 0) {
            PacketHelper.sendPacket(ServerboundMovePlayerPacket.Pos(vec.subtract(move.scale(offset * i)), false, false))
        }
//        PacketHelper.sendPacket(ServerboundMovePlayerPacket.Pos(vec.subtract(move.scale(offset)), false, false))
//        PacketHelper.sendPacket(ServerboundMovePlayerPacket.Pos(Vec3(vec.x, vec.y, vec.z), false, false))
    }

    private fun validPacket(packet: Packet<ServerPacketListener>): Boolean {
        if (packet is ServerboundUseItemPacket) return true
        else if (packet is ServerboundPlayerActionPacket) {
            return packet.action == ServerboundPlayerActionPacket.Action.RELEASE_USE_ITEM
        }
        return false
    }
}