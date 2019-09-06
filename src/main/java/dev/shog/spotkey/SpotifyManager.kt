package dev.shog.spotkey

import dev.shog.spotkey.handle.HotKeyLoader
import dev.shog.spotkey.tray.Tray
import dev.shog.spotkey.ui.Debug
import dev.shog.spotkey.ui.Error
import org.slf4j.LoggerFactory
import java.awt.GraphicsEnvironment
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

val LOGGER = LoggerFactory.getLogger("SpotKey")!!
const val VERSION = "1.0.0-B2"

/**
 * If the JVM is headless (no display).
 */
val HEADLESS: Boolean = try {
    val sc = GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices

    sc == null || sc.isEmpty()
} catch (ex: Exception) {
    true
}

/**
 * If SpotKey is in testing mode. This will allow the device to be headless.
 */
var test: Boolean = false

fun main(args: Array<String>) {
    if (args.isNotEmpty()) {
        for (arg in args) {
            if (arg.equals("--test", true))
                test = true
        }
    }

    if (!test && HEADLESS) {
        Error.make("You cannot run this without a display!")
        return
    }

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