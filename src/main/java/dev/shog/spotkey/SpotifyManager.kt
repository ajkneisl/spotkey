package dev.shog.spotkey

import dev.shog.spotkey.handle.HotKeyLoader
import org.slf4j.LoggerFactory
import dev.shog.spotkey.obj.HotKey
import dev.shog.spotkey.tray.Tray
import kotlin.system.exitProcess

val LOGGER = LoggerFactory.getLogger("SpotKey")!!
const val VERSION = "1.0.0-A2"

fun main() {
    Spotify.login()

    if (!Spotify.isPremium()) {
        LOGGER.error("This application requires your account to have Premium!")
        exitProcess(-1)
    }

    // Initializes the HotKeyLoader
    HotKeyLoader

    Tray.initTray()

    LOGGER.info("Started SpotKey v$VERSION")
}