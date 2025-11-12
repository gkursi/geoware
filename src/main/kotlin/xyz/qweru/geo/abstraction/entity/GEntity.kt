package xyz.qweru.geo.abstraction.entity

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import xyz.qweru.geo.core.Global

object GEntity {
    fun getAttackCharge(base: Float = 0.5f, entity: Entity? = Global.mc.player): Float {
        if (entity == null || entity !is Player) return 1f
        return entity.getAttackStrengthScale(base)
    }
}