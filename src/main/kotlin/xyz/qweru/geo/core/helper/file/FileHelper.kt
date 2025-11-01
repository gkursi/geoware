package xyz.qweru.geo.core.helper.file

import java.io.File
import java.nio.file.Path

object FileHelper {
    fun findOrCreateDir(parent: File, name: String): File {
        val dir = parent.resolve(name)
        if (!dir.exists() || !dir.isDirectory) dir.mkdir()
        return dir
    }

    fun findOrCreateDir(parent: Path, name: String): File = findOrCreateDir(parent.toFile(), name)
}