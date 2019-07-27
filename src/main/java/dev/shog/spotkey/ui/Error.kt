package dev.shog.spotkey.ui

import dev.shog.spotkey.handle.HotKeyHandler
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
        HotKeyHandler.beep()
        val panel = JFrame("SpotKey - Error!")

        panel.contentPane.add(ErrorUI(error))

        panel.pack()

        panel.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE

        panel.isVisible = true
    }

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