package xyz.qweru.geo.extend.minecraft.game

import net.minecraft.world.entity.player.Input

fun Input.withJump(jump: Boolean): Input =
    Input(this.forward, this.backward, this.left, this.right, jump, this.shift, this.sprint)
fun Input.withSneak(sneak: Boolean): Input =
    Input(this.forward, this.backward, this.left, this.right, this.jump, sneak, this.sprint)
fun Input.reverse(): Input =
    Input(this.backward, this.forward, this.right, this.left, this.jump, this.shift, this.sprint)