package xyz.qweru.geo.client.helper.version

import com.viaversion.viafabricplus.ViaFabricPlus
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion

object ViaHelper {
    fun isReverseHitOrder(): Boolean =
        ViaFabricPlus.getImpl().targetVersion.version <= ProtocolVersion.v1_8.version
}