package dev.shog.spotkey.util

import dev.shog.spotkey.ui.Error
import org.apache.commons.lang3.SystemUtils
import java.io.File
import kotlin.system.exitProcess

/**
 * OS Utils.
 */
object OsUtil {
    /**
     * The separator between files.
     */
    val separator = File.separator

    /**
     * Get the location where SpotKey files should be stored.
     */
    fun getDefaultLocation(): String {
        return when {
            SystemUtils.IS_OS_WINDOWS_10 -> System.getenv("appdata")
            SystemUtils.IS_OS_LINUX -> "/etc"
            else -> {
                Error.make("Invalid Operating System!")
                exitProcess(-1)
            }
        }
    }
}