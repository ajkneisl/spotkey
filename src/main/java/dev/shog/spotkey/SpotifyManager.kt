package dev.shog.spotkey

import dev.shog.spotkey.handle.HotKeyLoader
import org.slf4j.LoggerFactory
import dev.shog.spotkey.obj.HotKey
import kotlin.system.exitProcess

val LOGGER = LoggerFactory.getLogger("SpotKey")!!
const val VERSION = "1.0.0-A1"

fun main() {
    Spotify.login()

    if (!Spotify.isPremium()) {
        LOGGER.error("This application requires your account to have Premium!")
        exitProcess(-1)
    }

    // Initializes the HotKeyLoader
    HotKeyLoader

    LOGGER.info("Started SpotKey v$VERSION")
}