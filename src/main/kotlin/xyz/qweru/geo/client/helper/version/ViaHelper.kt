package xyz.qweru.geo.client.helper.version

import net.minecraft.core.Direction

object ViaHelper {
    fun isReverseHitOrder(): Boolean =
//        ViaFabricPlus.getImpl().targetVersion.version <= ProtocolVersion.v1_8.version
        true

    fun getReleaseItemDirection(): Direction =
//        if (ViaFabricPlus.getImpl().targetVersion.version < ProtocolVersion.v1_8.version) {
//            Direction.SOUTH
//        } else {
            Direction.DOWN
//        }
}