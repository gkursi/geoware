package xyz.qweru.geo.core.manager.combat

// TODO: somehow detect sweeps
/**
 * @param crit See AttackHelper#canCrit
 * @param critPossible See AttackHelper#willCrit
 *
 * @see xyz.qweru.geo.client.helper.player.AttackHelper.willCrit
 * @see xyz.qweru.geo.client.helper.player.AttackHelper.canCrit
 */
data class Attack(
    @Volatile var sprint: Boolean = false,
    @Volatile var crit: Boolean = false,
    @Volatile var critPossible: Boolean = false,
    @Volatile var ranged: Boolean = false
) {
    fun reset() {
        sprint = false
        crit = false
        critPossible = false
    }
}
