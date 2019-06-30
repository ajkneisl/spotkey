package dev.shog.spotkey

import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext
import com.wrapper.spotify.model_objects.specification.User
import java.lang.Exception
import java.lang.StringBuilder
import java.util.concurrent.ConcurrentHashMap

enum class DataType {
    PLAYLIST_URI, TRACK_URI
}
/**
 * This is used to save data grabbed from the configuration file.
 */
val DATA = ConcurrentHashMap<String, Pair<DataType, String>>()

/**
 * If it's currently playing anything.
 */
fun isCurrentlyPlaying(): Boolean {
    val x = try {
        Spotify.SPOTIFY_API.informationAboutUsersCurrentPlayback.build().execute()
    } catch (ex: Exception) {
        return false
    } ?: return false

    return x.is_playing ?: false
}

/**
 * Information about what the user is currently playing
 */
fun getCurrentPlayingData(): CurrentlyPlayingContext = Spotify.SPOTIFY_API.informationAboutUsersCurrentPlayback.build().execute()

/**
 * Gets the current user's data
 */
fun getUserData(): User = Spotify.SPOTIFY_API.currentUsersProfile.build().execute()

/**
 * Debugs the current playing data
 */
fun debug() {
    if (isCurrentlyPlaying()) {
        val data = getCurrentPlayingData()
        val sb = StringBuilder()

        for (artist in data.item.artists) sb.append("${artist.name}, ")

        val artists = sb.toString().removeSuffix(", ")

        LOGGER.debug("Device: Name = ${data.device.name}, Type = ${data.device.type}")
        LOGGER.debug("Timestamp: ${data.timestamp}")
        LOGGER.debug("Volume: ${data.device.volume_percent}%")
        LOGGER.debug("Currently Playing: Name = ${data.item.name}, URI = ${data.item.uri}, Album = ${data.item.album.name}, Artists = $artists")
        LOGGER.debug("State: Shuffle = ${data.shuffle_state}, Repeat = ${data.repeat_state}")
        LOGGER.debug("Progress: ${data.progress_ms}")
    } else LOGGER.warn("Currently not playing anything.")
}
