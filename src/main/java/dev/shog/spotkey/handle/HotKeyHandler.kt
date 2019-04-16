package dev.shog.spotkey.handle

import com.tulskiy.keymaster.common.Provider
import dev.shog.spotkey.handle.HotKeyLoader.hotKeys
import java.awt.Toolkit


/**
 * Manages hot keys throughout spotify manager
 */
object HotKeyHandler {
    /**
     * The current keymaster provider.
     */
    private val PROVIDER = Provider.getCurrentProvider(true)

    /**
     * Refreshes the currently loaded keybinds.
     */
    fun refreshCurrentlyLoaded() {
        PROVIDER.reset()

        hotKeys.forEach { k ->
            PROVIDER.register(k.getKeyStroke()) { k.execute() }
        }
    }

    /**
     * Plays a beep noise, an indicator of an issue.
     */
    fun beep() = Toolkit.getDefaultToolkit().beep()
}