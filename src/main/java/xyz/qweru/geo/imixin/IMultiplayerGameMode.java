package xyz.qweru.geo.imixin;

import net.minecraft.client.multiplayer.prediction.PredictiveAction;

public interface IMultiplayerGameMode {
    void geo_sequencedPacket(PredictiveAction action);
}
