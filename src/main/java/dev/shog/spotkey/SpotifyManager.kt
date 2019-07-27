package dev.shog.spotkey

import dev.shog.spotkey.handle.HotKeyLoader
import dev.shog.spotkey.tray.Tray
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

val LOGGER = LoggerFactory.getLogger("SpotKey")!!
const val VERSION = "1.0.0-B1"

fun main() {
    Spotify.login()

    do {
        TimeUnit.MILLISECONDS.sleep(500)
        LOGGER.debug("Waiting for code input...")
    } while (!Spotify.clientAvailable)

    LOGGER.debug("Input received!")

    if (!Spotify.isPremium()) {
        LOGGER.error("This application requires your account to have Premium!")
        exitProcess(-1)
    }

    // Initializes the HotKeyLoader
    HotKeyLoader

    Tray.initTray()

    LOGGER.info("Started SpotKey v$VERSION")
}