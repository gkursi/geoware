package xyz.qweru.geo.extend.kotlin.file

import xyz.qweru.geo.core.helper.file.FileHelper
import java.io.File
import java.nio.file.Path

fun Path.findOrCreateDir(name: String): File = FileHelper.findOrCreateDir(this, name)
fun File.findOrCreateDir(name: String): File = FileHelper.findOrCreateDir(this, name)
