package xyz.qweru.geo.core.helper.tree

import xyz.qweru.geo.core.system.impl.module.Module
import xyz.qweru.geo.core.system.impl.setting.Setting

/**
 * (parent, child) -> allow action on child
 */
fun interface SettingFilter : (Module, Setting<*, *>) -> Boolean