package dev.shog.spotkey

import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext
import com.wrapper.spotify.model_objects.specification.User
import dev.shog.spotkey.ui.Debug
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
 * Gets the current user's data
 */
fun getUserData(): User = Spotify.SPOTIFY_API.currentUsersProfile.build().execute()

/**
 * Debugs the current playing data
 */
fun debug() {
    if (Spotify.isCurrentlyPlaying()) {
        val data = Spotify.getCurrentPlayingData()
        val sb = StringBuilder()

        for (artist in data.item.artists) sb.append("${artist.name}, ")

        val artists = sb.toString().removeSuffix(", ")

        Debug.make(buildString {
            append("Device: Name = ${data.device.name}, Type = ${data.device.type}, ID = ${data.device.id}")
            append("\nTimestamp: ${data.timestamp}")
            append("\nVolume: ${data.device.volume_percent}%")
            append("\nCurrently Playing:")
            append("\n    - Name: ${data.item.name}")
            append("\n    - URI: ${data.item.uri}")
            append("\n    - Album: ${data.item.album.name}")
            append("\n    - Artists: $artists")
            append("\nState: Shuffle = ${data.shuffle_state}, Repeat = ${data.repeat_state}")
            append("\nProgress: ${data.progress_ms}")
        }, "Song Information")
    } else LOGGER.warn("Currently not playing anything.")
}
