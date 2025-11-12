package xyz.qweru.geo.extend.minecraft.game

import net.minecraft.world.entity.player.Input

fun Input.withJump(jump: Boolean): Input =
    Input(this.forward, this.backward, this.left, this.right, jump, this.shift, this.sprint)