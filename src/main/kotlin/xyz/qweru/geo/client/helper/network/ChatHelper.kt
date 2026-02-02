package xyz.qweru.geo.client.helper.network

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import xyz.qweru.geo.core.Core
import xyz.qweru.geo.core.Core.mc
import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.extend.minecraft.game.thePlayer
import java.awt.Color

object ChatHelper {
    private val prefix
        get() = Component.empty()
            .append(Component.literal("[")
                .withColor(Color.gray.rgb)
            )
            .append(Component.literal("Geo")
                .withStyle(Style.EMPTY
                    .withColor(Color(100, 250, 100).rgb)
                    .withBold(true)
                )
            )
            .append(Component.literal("]")
                .withColor(Color.gray.rgb)
            )
            .append(Component.literal(" > ")
                .withColor(Color.gray.rgb)
            )
    private val enable
        get() = Component.literal("")
            .withColor(Color.green.darker().rgb)
    private val disable
        get() = Component.literal("")
            .withColor(Color.red.rgb)
    private val warning
        get() = Component.literal("")
            .withStyle(Style.EMPTY
                .withColor(Color.orange.rgb)
                .withItalic(true)
            )
    private val error
        get() = Component.literal("")
            .withStyle(Style.EMPTY
                .withColor(Color.red.darker().rgb)
                .withBold(true)
            )

    fun log(text: Component) {
        if (mc.player == null) return
        mc.thePlayer.displayClientMessage(prefix.append(text), false)
    }

    fun toggleMessage(module: Module) =
        log((if (module.enabled) enable else disable).append(module.name))

    fun info(string: String) =
        log(Component.literal(string))

    fun warning(string: String) =
        log(warning.append(string))

    fun error(string: String) =
        log(error.append(string))
}