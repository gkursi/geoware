package xyz.qweru.geo.imixin;

import java.util.Optional;
import java.util.UUID;

public interface IUser {
    void geo_setToken(String session);
    void geo_setUsername(String username);
    void geo_setUuid(UUID uuid);
    void geo_setClientId(Optional<String> id);
    void geo_setXid(Optional<String> id);
}
