package dev.shog.spotkey.obj

import dev.shog.spotkey.LOGGER
import dev.shog.spotkey.Spotify.SPOTIFY_API
import dev.shog.spotkey.handle.HotKeyLoader
import dev.shog.spotkey.isCurrentlyPlaying
import org.json.JSONArray
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.util.concurrent.ConcurrentHashMap
import javax.swing.KeyStroke

/**
 * Holds a loaded Hot Key
 */
class HotKey(private val keystrokeString: String, private val actions: JSONArray) {
    init {
        validate()
        generate()
    }

    /**
     * The keystroke that executes the HotKey
     */
    private var keystroke: KeyStroke? = null

    /**
     * If the keystroke is valid
     */
    var valid: Boolean = false

    /**
     * If the variable generatedValues has been created.
     */
    private var generated: Boolean = false

    /**
     * The different actions the HotKey executes.
     */
    private var generatedValues: ArrayList<Action>? = null

    /**
     * Generates a executable Thread for when the HotKey is executed
     */
    private fun generate() {
        val ia = ArrayList<Action>()

        for (i in 0 until actions.length()) {
            val ob = actions.get(i) as? Int ?: continue

            ia.add(getActionFromInteger(ob))
        }

        generatedValues = ia
        generated = true
    }

    /**
     * Parses a keystroke from a string. If it's valid, valid will equal true.
     */
    private fun validate() {
        if (keystrokeString.isBlank()) {
            valid = false
            return
        }

        keystroke = KeyStroke.getKeyStroke(keystrokeString)

        if (keystroke == null) {
            valid = false
            LOGGER.error("$keystrokeString is invalid!")
        }
    }

    /**
     * If the HotKey is valid, get the keystroke.
     */
    fun getKeyStroke(): KeyStroke? = if (valid) keystroke else { validate(); keystroke }

    /**
     * Executes the HotKey
     */
    fun execute() {
        if (!valid) validate()

        for (a in generatedValues!!) {
            if (a.run()) a.action.run() else LOGGER.warn("Waiting for cool-down...")
        }
    }

    companion object {
        private fun getActionFromInteger(int: Int): Action = ACTIONS[int] ?: throw IllegalArgumentException("Illegal input for Action")

        private val ACTIONS = object : ConcurrentHashMap<Int, Action>() {
            init {
                this[0] = Action(Thread {
                    if (isCurrentlyPlaying()) SPOTIFY_API.skipUsersPlaybackToNextTrack().build().execute()
                }, 100)

                this[1] = Action(Thread {
                    if (isCurrentlyPlaying()) SPOTIFY_API.skipUsersPlaybackToPreviousTrack().build().execute()
                }, 100)

                this[2] = Action(Thread {
                    HotKeyLoader.reloadHotKeys()
                }, 10000)
            }
        }
    }

    /**
     * An action that a HotKey can activate.
     */
    internal class Action(val action: Thread, private val coolDown: Long) {
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

}