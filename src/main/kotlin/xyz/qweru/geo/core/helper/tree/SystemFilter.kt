package xyz.qweru.geo.core.helper.tree

import xyz.qweru.geo.core.system.System

/**
 * (parent, child) -> allow action on child
 */
fun interface SystemFilter : (System, System) -> Boolean