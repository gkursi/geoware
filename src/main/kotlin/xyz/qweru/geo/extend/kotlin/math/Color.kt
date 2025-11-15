@file:Suppress("NOTHING_TO_INLINE")
package xyz.qweru.geo.extend.kotlin.math

import java.awt.Color

inline fun Color.withAllowedAlpha(allowAlpha: Boolean) = Color(rgb, allowAlpha)
inline fun Color.withAlpha(alpha: Int) = Color(this.rgb or ((alpha and 0xFF) shl 24))
inline fun Color.withRed(red: Int) = Color(this.rgb or ((red and 0xFF) shl 16))
inline fun Color.withGreen(green: Int) = Color(this.rgb or ((green and 0xFF) shl 8))
inline fun Color.withBlue(blue: Int) = Color(this.rgb or (blue and 0xFF))
