package dev.shog.spotkey.handle

import com.tulskiy.keymaster.common.Provider
import dev.shog.spotkey.Spotify
import dev.shog.spotkey.isCurrentlyPlaying
import org.slf4j.LoggerFactory
import java.awt.Toolkit
import javax.swing.KeyStroke


/**
 * Manages hot keys throughout spotify manager
 */
object HotKeyHandler {
    private val LOGGER = LoggerFactory.getLogger(this.javaClass)!!

    /**
     * All of the keybinded actions
     */
    enum class Actions(val key: KeyStroke, val unit: () -> Unit) {
        NEXT_SONG(KeyStroke.getKeyStroke("control shift N"), {
            if (isCurrentlyPlaying())
                Spotify.SPOTIFY_API.skipUsersPlaybackToNextTrack().build().execute()
            else beep()
        }),

        PREVIOUS_SONG(KeyStroke.getKeyStroke("control shift L"), {
            if (isCurrentlyPlaying())
                Spotify.SPOTIFY_API.skipUsersPlaybackToPreviousTrack().build().execute()
            else beep()
        }),

        NEXT_PLAYLIST(KeyStroke.getKeyStroke("control shift alt N"), {

        }),

        PREVIOUS_PLAYLIST(KeyStroke.getKeyStroke("control shift alt L"), {

        }),
    }

    /**
     * Initializes all of the keybinds.
     *
     * Useful to allow init only when the client is ready.
     */
    fun init() {
        val provider = Provider.getCurrentProvider(true)

        for (action in Actions.values()) {
            provider.register(action.key) {
                LOGGER.debug("Running hotkey ${action.key}...")
                action.unit.invoke()
            }
        }
    }

    /**
     * Plays a beep noise, an indicator of an issue.
     */
    fun beep() = Toolkit.getDefaultToolkit().beep()
}