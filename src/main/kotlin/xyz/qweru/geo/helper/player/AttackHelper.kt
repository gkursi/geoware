package xyz.qweru.geo.helper.player

import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.decoration.EndCrystalEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.AxeItem
import net.minecraft.item.Items
import xyz.qweru.geo.core.Glob
import xyz.qweru.geo.extend.thePlayer

object AttackHelper {
    fun canAttack(target: Entity, playerWeaponOnly: Boolean = false): Boolean {
        if (target is EndCrystalEntity) return target.isAlive
        if (HotbarHelper.isInMainhand { st -> st.item is AxeItem } && target is LivingEntity && target.activeItem.isOf(Items.SHIELD))
            return true
        if (!HotbarHelper.isInMainhand { st -> HotbarHelper.isSword(st.item) || st.item is AxeItem } && target is PlayerEntity && playerWeaponOnly)
            return false

        return Glob.mc.thePlayer.getAttackCooldownProgress(0.5f) >= 1f
    }
}
