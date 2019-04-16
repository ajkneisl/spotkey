package dev.shog.spotkey

import dev.shog.spotkey.handle.HotKeyLoader
import org.slf4j.LoggerFactory
import dev.shog.spotkey.obj.HotKey

val LOGGER = LoggerFactory.getLogger("SpotKey")!!
val VERSION = "1.0.0-SNAPSHOT"

fun main() {
    Spotify.login()

    if (!Spotify.isPremium()) {
        LOGGER.error("This application requires your account to have Premium!")
        System.exit(-1)
    }

    // Initializes the HotKeyLoader
    HotKeyLoader

    LOGGER.info("Started SpotKey v$VERSION")
}