package xyz.qweru.geo.core.helper.tree

import xyz.qweru.geo.core.system.module.Module
import xyz.qweru.geo.core.system.setting.Setting

/**
 * (parent, child) -> allow action on child
 */
fun interface SettingFilter : (Module, Setting<*, *>) -> Boolean