package dev.shog.spotkey

import dev.shog.spotkey.handle.HotKeyLoader
import java.lang.NumberFormatException
import java.util.concurrent.ConcurrentHashMap
import kotlin.Exception

/**
 * An action that a HotKey can activate.
 */
class Action(val action: Thread, private val coolDown: Long, private vararg val args: Pair<String, Any>) {
    private var lastRan = 0L

    /**
     * Executes the action
     */
    fun run(): Boolean {
        if (System.currentTimeMillis() - lastRan >= coolDown) {
            lastRan = System.currentTimeMillis()
            return true
        }

        return false
    }
}

/**
 * Preset actions are actions that are very default-y; some actions are only available as preset actions as there's nothing available to change.
 */
val PRESET_ACTIONS = object : ConcurrentHashMap<Int, Action>() {
    init {
        /**
         * Skips to next song.
         */
        this[0] = Action(Thread {
            if (Spotify.isCurrentlyPlaying()) Spotify.SPOTIFY_API.skipUsersPlaybackToNextTrack().build().execute()
        }, 100)

        /**
         * Skips to previous song.
         */
        this[1] = Action(Thread {
            if (Spotify.isCurrentlyPlaying()) Spotify.SPOTIFY_API.skipUsersPlaybackToPreviousTrack().build().execute()
        }, 100)

        /**
         * Reloads hotkeys
         */
        this[2] = Action(Thread {
            HotKeyLoader.reloadHotKeys()
        }, 10000)

        /**
         * +1s the current playback volume.
         */
        this[3] = Action(Thread {
            if (Spotify.isCurrentlyPlaying()) Spotify.SPOTIFY_API.setVolumeForUsersPlayback(Spotify.getCurrentPlayingData().device.volume_percent + 1)
        }, 100)

        /**
         * Gets information about the current song.
         */
        this[4] = Action(Thread {
            debug()
        }, 100)

        /**
         * Pause or unpause user's playback.
         */
        this[5] = Action(Thread {
            if (!Spotify.isCurrentlyPlaying())
                try {
                    Spotify.SPOTIFY_API.startResumeUsersPlayback().build().execute()
                } catch (ex: Exception) {
                    // If it can't just resume, try to get the user's selected device. If it's not been selected, use the first one found.
                    val cfg = HotKeyLoader.getCfg()

                    // The default device must be open and available to spotify, or else it will pick the first available.
                    if (cfg.has("default-device")) {
                        val defaultDevice = cfg.get("default-device") as? String ?: return@Thread

                        try {
                            Spotify.SPOTIFY_API.startResumeUsersPlayback()
                                    .device_id(defaultDevice)
                                    .build()
                                    .execute()
                            return@Thread
                        } catch (ex: Exception) { }
                    }

                    Spotify.SPOTIFY_API.startResumeUsersPlayback()
                            .device_id(Spotify.SPOTIFY_API.usersAvailableDevices.build().execute().first().id)
                            .build()
                            .execute()
                }
            else try {
                Spotify.SPOTIFY_API.pauseUsersPlayback().build().execute()
            } catch (ex: Exception) {
                LOGGER.warn("Could not pause playback!")
            }
        }, 100)
    }
}

/**
 * Safely gets an action, and reports if it fails.
 */
fun getAction(int: Int): Action = PRESET_ACTIONS[int] ?: throw IllegalArgumentException("Illegal input for Action ($int)")

/**
 * Gets a detailed action.
 *
 * A detailed action can be put as id:tag|val:tag|val
 *
 * Something like 3:incr|50. This would make the volume go up 50 every increment.
 * Or , 3:incr|100. This would max the volume.
 */
fun getDetailedAction(int: Int, args: HashMap<String, Any>): Action? {
    when (int) {
        /**
         * Skips a variable amount of tracks.
         */
        0 -> {
            val tracks = if (args.containsKey("tracks")) {
                try {
                    args.getValue("tracks").toString().toLong()
                } catch (ex: NumberFormatException) { throw ex }
            } else 1

            return Action(Thread {
                if (Spotify.isCurrentlyPlaying()) {
                    for (i in 0 until tracks)
                        Spotify.SPOTIFY_API.skipUsersPlaybackToNextTrack().build().execute()
                }
            }, 100)
        }

        /**
         * Skips a variable amount of tracks behind.
         */
        1 -> {
            val tracks = if (args.containsKey("tracks")) {
                try {
                    args.getValue("tracks").toString().toLong()
                } catch (ex: NumberFormatException) { throw ex }
            } else 1

            return Action(Thread {
                if (Spotify.isCurrentlyPlaying()) {
                    for (i in 0 until tracks)
                        Spotify.SPOTIFY_API.skipUsersPlaybackToPreviousTrack().build().execute()
                }
            }, 100)
        }

        2 -> return PRESET_ACTIONS[2]!! // There's nothing that can be done to modify this.

        /**
         * Allows a variable amount of volume to be decreased or increased.
         */
        3 -> {
            when {
                // If they're trying to decrease the volume by a specified amount.
                args.containsKey("decr") -> {
                    val decr = try {
                        args.getValue("decr").toString().toInt()
                    } catch(ex: NumberFormatException) { throw ex }

                    if (0 > decr || 100 < decr) throw IllegalArgumentException("Invalid Volume")

                    return Action(Thread {
                        if (Spotify.isCurrentlyPlaying()) {
                            var decrease = Spotify.getCurrentPlayingData().device.volume_percent - decr

                            if (0 > decrease) decrease = 0

                            Spotify.SPOTIFY_API.setVolumeForUsersPlayback(decrease).build().execute()
                        }
                    }, 100)
                }

                // If they're trying to set the volume to an exact amount.
                args.containsKey("exa") -> {
                    val exa = try {
                        args.getValue("exa").toString().toInt()
                    } catch (ex: NumberFormatException) { throw ex }

                    if (0 > exa || 100 < exa) throw IllegalArgumentException("Invalid Volume")

                    return Action(Thread {
                        if (Spotify.isCurrentlyPlaying()) {
                            Spotify.SPOTIFY_API.setVolumeForUsersPlayback(exa).build().execute()
                        }
                    }, 100)
                }

                // Assume this is for increasing the volume.
                else -> {
                    val volumeIncrement = if (args.containsKey("incr")) {
                        try {
                            args.getValue("incr").toString().toInt()
                        } catch (ex: NumberFormatException) { throw ex }
                    } else 1

                    if (0 > volumeIncrement || 100 < volumeIncrement) throw IllegalArgumentException("Invalid Volume")

                    return Action(Thread {
                        if (Spotify.isCurrentlyPlaying()) {
                            var incr = Spotify.getCurrentPlayingData().device.volume_percent + volumeIncrement

                            if (incr > 100) incr = 100

                            Spotify.SPOTIFY_API.setVolumeForUsersPlayback(incr).build().execute()
                        }
                    }, 100)
                }
            }
        }

        4 -> return PRESET_ACTIONS[4]!! // There's nothing that can be done to modify this.
        5 -> return PRESET_ACTIONS[5]!! // Just pause/un-pauses
    }

    return null
}