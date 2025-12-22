package xyz.qweru.geo.client.helper.version

import com.viaversion.viafabricplus.ViaFabricPlus
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import net.minecraft.core.Direction

object ViaHelper {
    fun isReverseHitOrder(): Boolean =
        ViaFabricPlus.getImpl().targetVersion.version <= ProtocolVersion.v1_8.version

    fun getReleaseItemDirection(): Direction =
        if (ViaFabricPlus.getImpl().targetVersion.version < ProtocolVersion.v1_8.version) {
            Direction.SOUTH
        } else {
            Direction.DOWN
        }
}