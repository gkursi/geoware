package xyz.qweru.geo.extend

import net.minecraft.util.PlayerInput

fun PlayerInput.withJump(jump: Boolean): PlayerInput =
    PlayerInput(this.forward, this.backward, this.left, this.right, jump, this.sneak, this.sprint)