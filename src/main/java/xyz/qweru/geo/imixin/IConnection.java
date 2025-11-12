package xyz.qweru.geo.imixin;

import net.minecraft.network.protocol.Packet;

public interface IConnection {
    void geo_doSend(Packet<?> packet);
}
