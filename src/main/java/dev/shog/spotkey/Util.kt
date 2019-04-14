package dev.shog.spotkey

import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext
import com.wrapper.spotify.model_objects.specification.User

/**
 * If it's currently playing anything.
 */
fun isCurrentlyPlaying(): Boolean = Spotify.SPOTIFY_API.informationAboutUsersCurrentPlayback.build().execute().is_playing ?: false

/**
 * Information about what the user is currently playing
 */
fun getCurrentPlayingData(): CurrentlyPlayingContext = Spotify.SPOTIFY_API.informationAboutUsersCurrentPlayback.build().execute()

/**
 * Gets the current user's data
 */
fun getUserData(): User = Spotify.SPOTIFY_API.currentUsersProfile.build().execute()
