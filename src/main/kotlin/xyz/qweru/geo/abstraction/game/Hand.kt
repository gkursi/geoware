package xyz.qweru.geo.abstraction.game

import net.minecraft.world.InteractionHand

enum class Hand(val delegate: InteractionHand) {
    MAIN_HAND(InteractionHand.MAIN_HAND),
    OFF_HAND(InteractionHand.OFF_HAND)
}