package dev.shog.spotkey.ui

import dev.shog.spotkey.HEADLESS
import dev.shog.spotkey.LOGGER
import java.awt.Dimension
import java.awt.Font
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.WindowConstants

/**
 * Debug information
 */
object Debug {
    /**
     * Create a debug UI.
     */
    fun make(debug: String, title: String) {
        if (HEADLESS) {
            LOGGER.info(debug)
            return
        }

        val debugStr = "<html>" + debug.replace("\n", "<br />") + "</html>"

        val panel = JFrame("SpotKey - $title")

        panel.contentPane.add(DebugUI(debugStr, title))
        panel.pack()
        panel.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
        panel.isResizable = false
        panel.isVisible = true
    }

    /**
     * The debug UI
     */
    internal class DebugUI(dbgStr: String, title: String): JPanel() {
        private val debugLabel: JLabel = JLabel(title)
        private val debugText: JLabel = JLabel(dbgStr)

        init {
            preferredSize = Dimension(if (debugText.preferredSize.width < debugLabel.preferredSize.width)
                debugLabel.preferredSize.width + 48
                    else
                debugText.preferredSize.width + 48, 180)
            layout = null

            add(debugLabel)
            add(debugText)

            debugLabel.font = Font("Sans Serif", 0, 24)

            debugLabel.setBounds(15, 10, debugLabel.preferredSize.width, debugLabel.preferredSize.height)
            debugText.setBounds(45, 45, debugText.preferredSize.width, debugText.preferredSize.height)
        }
    }
}