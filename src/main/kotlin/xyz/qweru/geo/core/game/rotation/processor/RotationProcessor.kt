package xyz.qweru.geo.core.game.rotation.processor

import xyz.qweru.geo.core.game.rotation.data.Rotation

interface RotationProcessor {

    /**
     * @param delta current rotation delta
     * @return the new rotation delta
     */
    fun process(
        start: Rotation,
        end: Rotation,
        current: Rotation,
        delta: Rotation
    ): Rotation

}