package dev.shog.spotkey

import org.slf4j.LoggerFactory

val LOGGER = LoggerFactory.getLogger("SpotKey")!!

fun main() {
    Spotify.login()

    if (!Spotify.isPremium()) {
        LOGGER.error("This application requires your account to have Premium!")
        System.exit(-1)
    }

    LOGGER.info("spotkey")
}