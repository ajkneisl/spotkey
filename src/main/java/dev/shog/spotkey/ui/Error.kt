package dev.shog.spotkey.ui

import dev.shog.spotkey.HEADLESS
import dev.shog.spotkey.LOGGER
import dev.shog.spotkey.handle.HotKeyLoader
import java.awt.Dimension
import java.awt.Font
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.WindowConstants


/**
 * Create small UI bits to warn.
 */
object Error {
    /**
     * Create an error ui with [error].
     */
    fun make(error: String) {
        if (HEADLESS) {
            LOGGER.error(error)
            return
        }

        HotKeyLoader.beep()
        val panel = JFrame("SpotKey - Error!")

        panel.contentPane.add(ErrorUI(error))
        panel.pack()
        panel.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
        panel.isVisible = true
    }

    /**
     * The Error UI
     */
    internal class ErrorUI(errString: String): JPanel() {
        private val errorLabel: JLabel = JLabel("Error")
        private val errorText: JLabel = JLabel(errString)

        init {
            preferredSize = Dimension(345, 180)
            layout = null

            add(errorLabel)
            add(errorText)

            errorLabel.font = Font("Sans Serif", 0, 24)

            errorLabel.setBounds(15, 10, 1000, 20)
            errorText.setBounds(45, 45, 10000, 25)
        }
    }
}