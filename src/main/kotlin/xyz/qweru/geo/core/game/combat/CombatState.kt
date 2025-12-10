package xyz.qweru.geo.core.game.combat

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.Projectile
import xyz.qweru.basalt.EventPriority
import xyz.qweru.geo.client.event.EntityDamageEvent
import xyz.qweru.geo.client.helper.player.AttackHelper
import xyz.qweru.geo.core.Core.mc
import xyz.qweru.geo.core.event.Handler

class CombatState(private val playerProvider: (CombatState) -> Player?) {

    companion object {
        val SELF = CombatState { mc.player }
        val TARGET = CombatState { TargetTracker.target }
    }

    val player: Player?
        get() = playerProvider.invoke(this)
    @Volatile
    private var lastPlayer: Player? = player

    /// Last attack by the tracked player
    val lastAttack = Attack()
    /// Last attack to the tracked player
    val lastDamage = Attack()
    @Volatile
    var combo = 0
        private set

    @Handler(priority = EventPriority.FIRST)
    private fun onPacketReceive(e: EntityDamageEvent) {
        if (player == null) return
        val player = this.player!!

        if (player != lastPlayer) {
            combo = 0
            lastAttack.reset()
            lastDamage.reset()
            lastPlayer = player
        }

        if (e.directSourceEntity == player) {
            set(lastAttack, player)
            combo++
        } else if (e.entity == player) {
            set(lastDamage, e.directSourceEntity)
            combo = 0
        }
    }

    private fun set(attack: Attack, source: Entity?) {
        attack.crit = source is Player && AttackHelper.canCrit(source)
        attack.critPossible = source is Player && AttackHelper.willCrit(source)
        attack.sprint = source?.isSprinting ?: false
        attack.ranged = source is Projectile
    }

    fun predictNextAttack(): Attack =
        player?.let { pl -> Attack(pl.isSprinting, AttackHelper.canCrit(pl)) } ?: Attack()
}